package project.piuda.domain.aireport.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.aireport.application.dto.AiReportResponse;
import project.piuda.domain.aireport.domain.AiReport;
import project.piuda.domain.aireport.domain.AiReportRepository;
import project.piuda.domain.aireport.domain.CareRecommendation;
import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.dailylog.domain.HealthTrend;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiReportService {

    private final AiReportRepository aiReportRepository;
    private final DailyLogRepository dailyLogRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final PatientMemoryRepository patientMemoryRepository;
    private final UserRepository userRepository;
    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    @Transactional
    public AiReportResponse generateReport(Long patientId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);

        LocalDate thisMonday = LocalDate.now(ZoneId.of("Asia/Seoul")).with(DayOfWeek.MONDAY);

        if (aiReportRepository.existsByPatientIdAndWeekStart(patientId, thisMonday)) {
            throw new BusinessException("이번 주 리포트가 이미 생성되어 있습니다. 기존 리포트를 조회해주세요.");
        }

        List<DailyLog> thisWeekLogs = dailyLogRepository
                .findByPatientIdAndLogDateBetweenOrderByLogDateAsc(patientId, thisMonday, thisMonday.plusDays(6));
        List<DailyLog> lastWeekLogs = dailyLogRepository
                .findByPatientIdAndLogDateBetweenOrderByLogDateAsc(patientId, thisMonday.minusDays(7), thisMonday.minusDays(1));

        if (thisWeekLogs.isEmpty() && lastWeekLogs.isEmpty()) {
            throw new BusinessException("리포트 생성을 위한 일지 데이터가 없습니다. 최소 1개 이상의 일지를 작성해주세요.");
        }

        String patientContext = buildPatientContext(patient);
        String weekSummary = buildWeekSummary(thisWeekLogs, lastWeekLogs);
        List<Document> ragDocs = retrieveRelevantDocs(patient, thisWeekLogs);

        String systemPrompt = buildSystemPrompt(ragDocs);
        String userPrompt = buildUserPrompt(patient, patientContext, weekSummary);

        String rawResponse = chatModel.call(new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userPrompt)
        ))).getResult().getOutput().getText();

        CareRecommendation recommendation = extractRecommendation(rawResponse);

        AiReport report = aiReportRepository.save(AiReport.builder()
                .patient(patient)
                .weekStart(thisMonday)
                .content(rawResponse)
                .recommendation(recommendation)
                .build());

        return new AiReportResponse(report);
    }

    public List<AiReportResponse> getReports(Long patientId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);
        return aiReportRepository.findAllByPatientIdOrderByWeekStartDesc(patientId)
                .stream().map(AiReportResponse::new).toList();
    }

    public AiReportResponse getLatestReport(Long patientId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);
        return aiReportRepository.findTopByPatientIdOrderByWeekStartDesc(patientId)
                .map(AiReportResponse::new)
                .orElseThrow(() -> new NotFoundException("아직 생성된 리포트가 없습니다."));
    }

    public AiReportResponse getReport(Long patientId, Long reportId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);
        return new AiReportResponse(getOwnedReport(patientId, reportId, user));
    }

    @Transactional
    public void deleteReport(Long patientId, Long reportId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);
        aiReportRepository.delete(getOwnedReport(patientId, reportId, user));
    }

    private AiReport getOwnedReport(Long patientId, Long reportId, User user) {
        AiReport report = aiReportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 리포트입니다."));
        if (!report.getPatient().getId().equals(patientId)) {
            throw new ForbiddenException("해당 환자의 리포트가 아닙니다.");
        }
        return report;
    }

    // ─── 프롬프트 빌더 ──────────────────────────────────────────

    private String buildSystemPrompt(List<Document> ragDocs) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                당신은 치매 환자 돌봄 전문 AI 어시스턴트입니다.
                보호자가 환자의 상태를 이해하고 적절한 돌봄 결정을 내릴 수 있도록 주간 리포트를 작성합니다.

                리포트 작성 규칙:
                - 전문 의학 용어 대신 보호자가 이해하기 쉬운 일상 언어로 작성하세요.
                - 지난주 대비 이번 주 변화를 구체적으로 설명하세요.
                - 긍정적 변화와 주의가 필요한 변화를 균형 있게 서술하세요.
                - 보호자가 즉시 실천할 수 있는 행동 가이드를 제시하세요.
                - 돌봄 방향 추천(재가 돌봄/요양원/현상 유지)을 명확히 제시하세요.
                - 응답 마지막 줄에 반드시 다음 형식으로 추천을 명시하세요:
                  [추천: HOME_CARE] 또는 [추천: NURSING_HOME] 또는 [추천: MAINTAIN]
                """);

        if (!ragDocs.isEmpty()) {
            sb.append("\n[참고 전문 지식 - 치매 케어 가이드라인]\n");
            for (Document doc : ragDocs) {
                sb.append(doc.getText()).append("\n---\n");
            }
        }
        return sb.toString();
    }

    private String buildUserPrompt(Patient patient, String patientContext, String weekSummary) {
        return String.format("""
                다음은 %s 환자의 주간 돌봄 데이터입니다.

                %s

                %s

                위 데이터를 바탕으로 주간 리포트를 작성해주세요.
                다음 항목을 포함해주세요:
                1. 📊 이번 주 돌봄 변화 요약 (지난주 대비)
                2. 💡 돌봄 방향 추천 및 이유
                3. 📌 보호자를 위한 이번 주 행동 가이드 (2~3가지)
                """,
                patient.getName(), patientContext, weekSummary);
    }

    private String buildPatientContext(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append("【환자 기본 정보】\n");
        sb.append("- 이름: ").append(patient.getName()).append("\n");
        if (patient.getBirthDate() != null) sb.append("- 생년월일: ").append(patient.getBirthDate()).append("\n");
        if (patient.getGender() != null) sb.append("- 성별: ").append(patient.getGender()).append("\n");
        if (patient.getDementiaStage() != null) sb.append("- 치매 단계: ").append(patient.getDementiaStage()).append("\n");

        patientMemoryRepository.findByPatientId(patient.getId()).ifPresent(memory -> {
            if (memory.getDementiaType() != null) sb.append("- 치매 유형: ").append(memory.getDementiaType()).append("\n");
            if (memory.getMedicationInfo() != null) sb.append("- 복용 약물: ").append(memory.getMedicationInfo()).append("\n");
            if (memory.getComorbidities() != null) sb.append("- 동반 질환: ").append(memory.getComorbidities()).append("\n");
        });
        return sb.toString();
    }

    private String buildWeekSummary(List<DailyLog> thisWeek, List<DailyLog> lastWeek) {
        StringBuilder sb = new StringBuilder();

        sb.append("【이번 주 일지 (").append(thisWeek.size()).append("개)】\n");
        if (thisWeek.isEmpty()) {
            sb.append("- 이번 주 기록된 일지가 없습니다.\n");
        } else {
            sb.append(summarizeLogs(thisWeek));
        }

        sb.append("\n【지난 주 일지 (").append(lastWeek.size()).append("개)】\n");
        if (lastWeek.isEmpty()) {
            sb.append("- 지난 주 기록된 일지가 없습니다.\n");
        } else {
            sb.append(summarizeLogs(lastWeek));
        }

        return sb.toString();
    }

    private String summarizeLogs(List<DailyLog> logs) {
        int totalPhysical = logs.stream().mapToInt(DailyLog::getPhysicalTotalMinutes).sum();
        int totalCognitive = logs.stream().mapToInt(l ->
                l.getCognitiveStimulationMinutes() + l.getCognitiveLifeTogetherMinutes() + l.getCognitiveBehaviorManagementMinutes()).sum();
        int totalEmotional = logs.stream().mapToInt(DailyLog::getEmotionalCommunicationMinutes).sum();
        int totalBowel = logs.stream().mapToInt(DailyLog::getBowelIncontinenceCount).sum();
        int totalUrine = logs.stream().mapToInt(DailyLog::getUrineIncontinenceCount).sum();

        long hygieneCount = logs.stream().filter(DailyLog::isPhysicalHygiene).count();
        long bathCount = logs.stream().filter(DailyLog::isPhysicalBath).count();
        long toiletCount = logs.stream().filter(DailyLog::isPhysicalToiletHelp).count();

        long worsenedPhysical = logs.stream().filter(l -> l.getPhysicalFunctionTrend() == HealthTrend.WORSENED).count();
        long improvedPhysical = logs.stream().filter(l -> l.getPhysicalFunctionTrend() == HealthTrend.IMPROVED).count();
        long worsenedMeal = logs.stream().filter(l -> l.getMealFunctionTrend() == HealthTrend.WORSENED).count();
        long improvedMeal = logs.stream().filter(l -> l.getMealFunctionTrend() == HealthTrend.IMPROVED).count();

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("- 신체활동 지원 총 %d분 (위생 %d회, 목욕 %d회, 화장실 도움 %d회)\n",
                totalPhysical, hygieneCount, bathCount, toiletCount));
        sb.append(String.format("- 인지 활동 총 %d분\n", totalCognitive));
        sb.append(String.format("- 정서 지원 총 %d분\n", totalEmotional));
        sb.append(String.format("- 실금 횟수: 대변 %d회, 소변 %d회\n", totalBowel, totalUrine));
        sb.append(String.format("- 신체 기능 트렌드: 호전 %d일, 악화 %d일\n", improvedPhysical, worsenedPhysical));
        sb.append(String.format("- 식사 기능 트렌드: 호전 %d일, 악화 %d일\n", improvedMeal, worsenedMeal));

        List<String> notes = logs.stream()
                .filter(l -> l.getSpecialNotes() != null && !l.getSpecialNotes().isBlank())
                .map(l -> l.getLogDate() + ": " + l.getSpecialNotes())
                .toList();
        if (!notes.isEmpty()) {
            sb.append("- 특이사항:\n");
            notes.forEach(n -> sb.append("  · ").append(n).append("\n"));
        }
        return sb.toString();
    }

    private List<Document> retrieveRelevantDocs(Patient patient, List<DailyLog> logs) {
        String query = "치매 " + (patient.getDementiaStage() != null ? patient.getDementiaStage().name() : "") +
                " 돌봄 요양원 재가 돌봄 추천 기준";
        try {
            return vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(3)
                            .similarityThreshold(0.5)
                            .build());
        } catch (Exception e) {
            log.warn("[AiReport] RAG 검색 실패, RAG 없이 생성합니다: {}", e.getMessage());
            return List.of();
        }
    }

    private CareRecommendation extractRecommendation(String content) {
        if (content.contains("[추천: NURSING_HOME]")) return CareRecommendation.NURSING_HOME;
        if (content.contains("[추천: HOME_CARE]")) return CareRecommendation.HOME_CARE;
        return CareRecommendation.MAINTAIN;
    }

    // ─── Private helpers ────────────────────────────────────────

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
