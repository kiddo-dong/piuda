package project.piuda.domain.dailylog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DailyLogService {

    private final DailyLogRepository dailyLogRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDailyLog(Long patientId, Long writerId, DailyLogRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다."));
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("작성자를 찾을 수 없습니다."));

        DailyLog dailyLog = DailyLog.builder()
                .patient(patient)
                .writer(writer)
                .logDate(request.getLogDate())
                .mealStatus(request.getMealStatus())
                .medicationStatus(request.isMedicationStatus())
                .sleepStatus(request.getSleepStatus())
                .walkStatus(request.isWalkStatus())
                .abnormalBehavior(request.getAbnormalBehavior())
                .generalNote(request.getGeneralNote())
                .build();

        return dailyLogRepository.save(dailyLog).getId();
    }
}