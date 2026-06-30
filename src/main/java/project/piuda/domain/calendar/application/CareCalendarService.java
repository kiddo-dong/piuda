package project.piuda.domain.calendar.application;

import project.piuda.domain.calendar.application.dto.CalendarOverviewResponse;
import project.piuda.domain.calendar.application.dto.CareCalendarRequest;
import project.piuda.domain.calendar.application.dto.CareCalendarResponse;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CareCalendarRepository;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogResponse;
import project.piuda.domain.carejudgment.domain.CareJudgmentLogRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
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
    private final CareJudgmentLogRepository careJudgmentLogRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createSchedule(Long patientId, String userEmail, CareCalendarRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User writer = getUser(userEmail);

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new NotFoundException("지정된 담당자가 존재하지 않는 유저입니다."));
        }

        CareCalendar calendar = CareCalendar.builder()
                .patient(patient)
                .writer(writer)
                .assignee(assignee)
                .title(request.getTitle())
                .content(request.getContent())
                .calendarType(CalendarType.SCHEDULE)
                .category(request.getCategory())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        return careCalendarRepository.save(calendar).getId();
    }

    public CalendarOverviewResponse getCalendarEvents(Long patientId, String userEmail) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        validatePatientAccess(patient, getUser(userEmail));

        List<CareCalendarResponse> calendars = careCalendarRepository
                .findByPatientIdOrderByStartTimeAsc(patientId).stream()
                .map(CareCalendarResponse::new)
                .collect(Collectors.toList());

        // 저장은 분리, 조회만 통합 — 해당 환자의 판단 기록을 함께 반환 (날짜 매핑은 createdAt 기준)
        List<CareJudgmentLogResponse> judgmentLogs = careJudgmentLogRepository
                .findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(CareJudgmentLogResponse::new)
                .collect(Collectors.toList());

        return new CalendarOverviewResponse(calendars, judgmentLogs);
    }

    public CareCalendarResponse getCalendarEvent(Long calendarId) {
        return new CareCalendarResponse(getCalendar(calendarId));
    }

    @Transactional
    public void updateSchedule(Long calendarId, String userEmail, CareCalendarRequest request) {
        CareCalendar calendar = getCalendar(calendarId);
        User requester = getUser(userEmail);

        if (!calendar.getWriter().getId().equals(requester.getId())) {
            throw new ForbiddenException("본인이 작성한 일정만 수정할 수 있습니다.");
        }

        User assignee = null;
        if (request.getAssigneeId() != null) {
            assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new NotFoundException("지정된 담당자가 존재하지 않는 유저입니다."));
        }

        calendar.updateSchedule(
                request.getTitle(),
                request.getContent(),
                assignee,
                request.getCategory(),
                request.getStartTime(),
                request.getEndTime()
        );
    }

    @Transactional
    public void deleteCalendarEvent(Long calendarId, String userEmail) {
        CareCalendar calendar = getCalendar(calendarId);
        User requester = getUser(userEmail);

        if (!calendar.getWriter().getId().equals(requester.getId())) {
            throw new ForbiddenException("본인이 작성한 일정만 삭제할 수 있습니다.");
        }

        careCalendarRepository.delete(calendar);
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

    private CareCalendar getCalendar(Long calendarId) {
        return careCalendarRepository.findById(calendarId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 일정입니다."));
    }
}
