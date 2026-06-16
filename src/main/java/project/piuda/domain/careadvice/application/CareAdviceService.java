package project.piuda.domain.careadvice.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.careadvice.application.dto.*;
import project.piuda.domain.careadvice.domain.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.RagChatClient;
import project.piuda.global.infrastructure.RagChatClient.RagResult;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareAdviceService {

    private static final int CONTEXT_MESSAGE_LIMIT = 10;
    private static final int SESSION_RETENTION_DAYS = 30;

    private final CareAdviceSessionRepository sessionRepository;
    private final CareAdviceMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final PatientMemoryRepository patientMemoryRepository;
    private final RagChatClient ragChatClient;

    @Transactional
    public CareAdviceSessionResponse createSession(Long patientId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));

        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }

        return new CareAdviceSessionResponse(sessionRepository.save(
                CareAdviceSession.builder().user(user).patient(patient).build()
        ));
    }

    @Transactional
    public SendCareAdviceResponse sendMessage(Long sessionId, String userEmail, CareAdviceMessageRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션에만 접근할 수 있습니다.");
        }

        List<CareAdviceMessage> recentHistory = messageRepository.findRecentMessages(
                sessionId, PageRequest.of(0, CONTEXT_MESSAGE_LIMIT)
        );
        Collections.reverse(recentHistory);

        CareAdviceMessage userMessage = messageRepository.save(
                CareAdviceMessage.builder()
                        .session(session)
                        .role(MessageRole.USER)
                        .content(request.getContent())
                        .build()
        );

        String patientContext = buildPatientContext(session.getPatient(), request.getContent());
        RagResult result = ragChatClient.sendMessage(recentHistory, request.getContent(), patientContext);

        CareAdviceMessage assistantMessage = messageRepository.save(
                CareAdviceMessage.builder()
                        .session(session)
                        .role(MessageRole.ASSISTANT)
                        .content(result.content())
                        .build()
        );

        return new SendCareAdviceResponse(
                new CareAdviceMessageResponse(userMessage),
                new CareAdviceMessageResponse(assistantMessage),
                result.ragUsed()
        );
    }

    @Transactional
    public void deleteSession(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션만 삭제할 수 있습니다.");
        }
        sessionRepository.delete(session);
    }

    public List<CareAdviceSessionResponse> getSessions(Long patientId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));

        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }

        return sessionRepository.findByUserIdAndPatientIdOrderByCreatedAtDesc(user.getId(), patientId)
                .stream()
                .map(CareAdviceSessionResponse::new)
                .collect(Collectors.toList());
    }

    public List<CareAdviceMessageResponse> getMessages(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션에만 접근할 수 있습니다.");
        }

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(CareAdviceMessageResponse::new)
                .collect(Collectors.toList());
    }

    // 매일 새벽 3시에 30일 이상 된 세션 자동 삭제
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupOldSessions() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(SESSION_RETENTION_DAYS);
        List<CareAdviceSession> oldSessions = sessionRepository.findByCreatedAtBefore(cutoff);
        if (!oldSessions.isEmpty()) {
            sessionRepository.deleteAll(oldSessions);
            log.info("[CareAdvice] 만료 세션 {}개 삭제 완료 (기준: {}일 이상)", oldSessions.size(), SESSION_RETENTION_DAYS);
        }
    }

    private String buildPatientContext(Patient patient, String question) {
        StringBuilder sb = new StringBuilder();
        sb.append("현재 돌보는 환자 정보:\n");
        sb.append("- 이름: ").append(patient.getName()).append("\n");
        if (patient.getBirthDate() != null) sb.append("- 생년월일: ").append(patient.getBirthDate()).append("\n");
        if (patient.getGender() != null) sb.append("- 성별: ").append(patient.getGender()).append("\n");
        if (patient.getDementiaStage() != null) sb.append("- 치매 단계: ").append(patient.getDementiaStage()).append("\n");

        patientMemoryRepository.findByPatientId(patient.getId()).ifPresent(memory -> {
            // 핵심 의료 정보는 질문 내용과 무관하게 항상 포함
            appendIfPresent(sb, "치매 유형", memory.getDementiaType());
            appendIfPresent(sb, "복용 약물", memory.getMedicationInfo());
            appendIfPresent(sb, "금기 사항", memory.getContraindications());
            appendIfPresent(sb, "동반 질환", memory.getComorbidities());
            appendIfPresent(sb, "혈액형", memory.getBloodType());
            appendIfPresent(sb, "장기요양 등급", memory.getLongTermCareGrade() > 0
                    ? String.valueOf(memory.getLongTermCareGrade()) : null);

            // 질문 키워드에 따라 관련 정보만 선택적으로 포함
            if (containsAny(question, "좋아", "취향", "선호", "음식", "음악", "활동")) {
                appendIfPresent(sb, "좋아하는 것", memory.getLikes());
            }
            if (containsAny(question, "싫어", "거부", "저항", "불안", "힘들")) {
                appendIfPresent(sb, "싫어하는 것", memory.getDislikes());
            }
            if (containsAny(question, "진정", "달래", "화", "소리", "울", "폭력", "공격")) {
                appendIfPresent(sb, "진정 효과 있는 말", memory.getSoothingWords());
                appendIfPresent(sb, "역효과 나는 말", memory.getIneffectiveWords());
            }
            if (containsAny(question, "저녁", "밤", "황혼", "석양", "해질", "일몰")) {
                appendIfPresent(sb, "석양증후군 정보", memory.getSundowningInfo());
            }
            if (containsAny(question, "반복", "같은 말", "계속", "또")) {
                appendIfPresent(sb, "반복 행동", memory.getRepetitiveBehaviors());
            }
            if (containsAny(question, "배회", "외출", "나가", "길", "잃어", "실종")) {
                appendIfPresent(sb, "배회 경로", memory.getWanderingRoute());
            }
            if (containsAny(question, "응급", "병원", "위급", "연락", "전화")) {
                appendIfPresent(sb, "응급 연락처", memory.getEmergencyContacts());
                appendIfPresent(sb, "주치의 정보", memory.getPrimaryDoctorInfo());
                appendIfPresent(sb, "선호 병원", memory.getPreferredHospital());
            }
            if (containsAny(question, "특이", "주의", "참고")) {
                appendIfPresent(sb, "특이사항", memory.getSpecialNotes());
            }
        });

        return sb.toString();
    }

    private boolean containsAny(String text, String... keywords) {
        if (text == null) return false;
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append("- ").append(label).append(": ").append(value).append("\n");
        }
    }
}
