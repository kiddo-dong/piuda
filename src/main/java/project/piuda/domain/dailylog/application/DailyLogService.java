package project.piuda.domain.dailylog.application;

import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogResponse;
import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    // 1. 일지 등록
    @Transactional
    public Long createDailyLog(Long patientId, String userEmail, DailyLogRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));
        User writer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 조건 검증: 간병인(CAREGIVER)이 아닌데 정서 지원 시간을 적은 경우 차단
        if (!"CAREGIVER".equals(writer.getRole().name()) && request.getEmotionalCommunicationMinutes() > 0) {
            throw new IllegalArgumentException("정서 지원(의사소통 도움) 항목은 간병인 권한만 기입할 수 있습니다.");
        }

        DailyLog dailyLog = DailyLog.builder()
                .patient(patient)
                .writer(writer)
                .logDate(request.getLogDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .physicalHygiene(request.isPhysicalHygiene())
                .physicalBath(request.isPhysicalBath())
                .physicalMealHelp(request.isPhysicalMealHelp())
                .physicalPositionChange(request.isPhysicalPositionChange())
                .physicalMobilityHelp(request.isPhysicalMobilityHelp())
                .physicalToiletHelp(request.isPhysicalToiletHelp())
                .physicalTotalMinutes(request.getPhysicalTotalMinutes())
                .cognitiveStimulationMinutes(request.getCognitiveStimulationMinutes())
                .cognitiveLifeTogetherMinutes(request.getCognitiveLifeTogetherMinutes())
                .cognitiveBehaviorManagementMinutes(request.getCognitiveBehaviorManagementMinutes())
                .emotionalCommunicationMinutes(request.getEmotionalCommunicationMinutes())
                .householdMealClean(request.isHouseholdMealClean())
                .householdPersonalHelp(request.isHouseholdPersonalHelp())
                .householdTotalMinutes(request.getHouseholdTotalMinutes())
                .physicalFunctionTrend(request.getPhysicalFunctionTrend())
                .mealFunctionTrend(request.getMealFunctionTrend())
                .bowelIncontinenceCount(request.getBowelIncontinenceCount())
                .urineIncontinenceCount(request.getUrineIncontinenceCount())
                .specialNotes(request.getSpecialNotes())
                .build();

        return dailyLogRepository.save(dailyLog).getId();
    }

    // 2. 환자별 일지 목록 조회
    public List<DailyLogResponse> getDailyLogs(Long patientId) {
        return dailyLogRepository.findByPatientIdOrderByLogDateDesc(patientId).stream()
                .map(DailyLogResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 일지 상세 조회
    public DailyLogResponse getDailyLogDetails(Long logId) {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일지입니다."));
        return new DailyLogResponse(log);
    }

    // 4. 일지 수정
    @Transactional
    public void updateDailyLog(Long logId, String userEmail, DailyLogRequest request) {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일지입니다."));
        User writer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // ★ 수정 시에도 간병인 권한 체크 검증
        if (!"CAREGIVER".equals(writer.getRole().name()) && request.getEmotionalCommunicationMinutes() > 0) {
            throw new IllegalArgumentException("정서 지원(의사소통 도움) 항목은 간병인 권한만 기입할 수 있습니다.");
        }

        log.update(
                request.getStartTime(), request.getEndTime(),
                request.isPhysicalHygiene(), request.isPhysicalBath(), request.isPhysicalMealHelp(),
                request.isPhysicalPositionChange(), request.isPhysicalMobilityHelp(), request.isPhysicalToiletHelp(),
                request.getPhysicalTotalMinutes(), request.getCognitiveStimulationMinutes(),
                request.getCognitiveLifeTogetherMinutes(), request.getCognitiveBehaviorManagementMinutes(),
                request.getEmotionalCommunicationMinutes(), request.isHouseholdMealClean(),
                request.isHouseholdPersonalHelp(), request.getHouseholdTotalMinutes(),
                request.getPhysicalFunctionTrend(), request.getMealFunctionTrend(),
                request.getBowelIncontinenceCount(), request.getUrineIncontinenceCount(),
                request.getSpecialNotes()
        );
    }

    // 5. 일지 삭제
    @Transactional
    public void deleteDailyLog(Long logId) {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일지입니다."));
        dailyLogRepository.delete(log);
    }
}