package project.piuda.domain.dailylog.application;

import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogResponse;
import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CareCalendarRepository;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.Role;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;
    private final CareCalendarRepository careCalendarRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Long createDailyLog(Long patientId, String userEmail, DailyLogRequest request, MultipartFile image) throws IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User writer = getUser(userEmail);

        validatePatientAccess(patient, writer);

        if (writer.getRole() != Role.CAREGIVER && request.getEmotionalCommunicationMinutes() > 0) {
            throw new ForbiddenException("정서 지원 항목은 간병인 권한만 기입할 수 있습니다.");
        }

        String imageUrl = (image != null && !image.isEmpty()) ? s3UploadService.upload(image, "daily-log") : null;

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
                .imageUrl(imageUrl)
                .build();

        DailyLog savedLog = dailyLogRepository.save(dailyLog);

        careCalendarRepository.save(CareCalendar.builder()
                .patient(patient)
                .writer(writer)
                .dailyLog(savedLog)
                .title(writer.getName() + "님의 하루 일지 작성 완료")
                .calendarType(CalendarType.DAILY_LOG)
                .startTime(request.getLogDate().atTime(request.getStartTime()))
                .endTime(request.getLogDate().atTime(request.getEndTime()))
                .build());

        return savedLog.getId();
    }

    public List<DailyLogResponse> getDailyLogs(Long patientId, String userEmail) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User user = getUser(userEmail);
        validatePatientAccess(patient, user);
        return dailyLogRepository.findByPatientIdOrderByLogDateDesc(patientId).stream()
                .map(DailyLogResponse::new)
                .collect(Collectors.toList());
    }

    public DailyLogResponse getDailyLogDetails(Long logId, String userEmail) {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 일지입니다."));
        User user = getUser(userEmail);
        validatePatientAccess(log.getPatient(), user);
        return new DailyLogResponse(log);
    }

    @Transactional
    public void updateDailyLog(Long logId, String userEmail, DailyLogRequest request, MultipartFile image) throws IOException {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 일지입니다."));
        User writer = getUser(userEmail);

        validatePatientAccess(log.getPatient(), writer);
        validateWriter(log, writer);

        if (writer.getRole() != Role.CAREGIVER && request.getEmotionalCommunicationMinutes() > 0) {
            throw new ForbiddenException("정서 지원 항목은 간병인 권한만 기입할 수 있습니다.");
        }

        String imageUrl = (image != null && !image.isEmpty()) ? s3UploadService.upload(image, "daily-log") : log.getImageUrl();

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
                request.getSpecialNotes(),
                imageUrl
        );
    }

    @Transactional
    public void deleteDailyLog(Long logId, String userEmail) {
        DailyLog log = dailyLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 일지입니다."));
        User user = getUser(userEmail);
        validatePatientAccess(log.getPatient(), user);
        validateWriter(log, user);
        careCalendarRepository.deleteByDailyLogId(logId);
        dailyLogRepository.delete(log);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }

    private void validateWriter(DailyLog log, User user) {
        if (!log.getWriter().getId().equals(user.getId())) {
            throw new ForbiddenException("본인이 작성한 일지만 수정/삭제할 수 있습니다.");
        }
    }
}
