package project.piuda.domain.calendar.application;

import project.piuda.domain.calendar.application.dto.CareCalendarRequest;
import project.piuda.domain.calendar.application.dto.CareCalendarResponse;
import project.piuda.domain.calendar.domain.CalendarCategory;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CareCalendarRepository;
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
public class CareCalendarService {

    private final CareCalendarRepository careCalendarRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    // 1. 수동 일정 등록 (SCHEDULE 유형 - 담당자 지정 포함)
    @Transactional
    public Long createSchedule(Long patientId, String userEmail, CareCalendarRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));
        User writer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 작성자입니다."));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("지정된 담당자가 존재하지 않는 유저입니다."));
            // 💡 [기획 연동 포인트] 이곳에서 assignee 유저에게 FCM 푸시 알림 발송 로직을 연계하면 됩니다.
        }

        CareCalendar calendar = CareCalendar.builder()
                .patient(patient)
                .writer(writer)
                .assignee(assignee)
                .title(request.getTitle())
                .content(request.getContent())
                .calendarType(CalendarType.SCHEDULE) // 수동 스케줄
                .category(request.getCategory())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return careCalendarRepository.save(calendar).getId();
    }

    // 2. 특정 환자의 공유 달력 일정 전체 조회
    public List<CareCalendarResponse> getCalendarEvents(Long patientId) {
        return careCalendarRepository.findByPatientIdOrderByStartTimeAsc(patientId).stream()
                .map(CareCalendarResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 수동 일정 수정
    @Transactional
    public void updateSchedule(Long calendarId, CareCalendarRequest request) {
        CareCalendar calendar = careCalendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new IllegalArgumentException("지정된 담당자가 존재하지 않는 유저입니다."));
        }

        // 엔티티 내부 비즈니스 로직 호출 (DAILY_LOG 유형 수정 시 예외 처리는 엔티티에 캡슐화 완료)
        calendar.updateSchedule(
                request.getTitle(),
                request.getContent(),
                assignee,
                request.getCategory(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    // 4. 일정 삭제
    @Transactional
    public void deleteCalendarEvent(Long calendarId) {
        CareCalendar calendar = careCalendarRepository.findById(calendarId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일정입니다."));
        careCalendarRepository.delete(calendar);
    }
}