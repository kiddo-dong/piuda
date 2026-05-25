package project.piuda.domain.calendar.application.dto;

import project.piuda.domain.calendar.domain.CareCalendar;
import project.piuda.domain.calendar.domain.CalendarType;
import project.piuda.domain.calendar.domain.CalendarCategory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CareCalendarResponse {
    private final Long id;
    private final Long patientId;
    private final String writerName;
    private final String assigneeName;  // 담당자 이름 (없으면 null)
    private final Long dailyLogId;      // 연동된 일지 ID (출처가 DAILY_LOG일 때만 존재)
    private final String title;
    private final String content;
    private final CalendarType calendarType;  // DAILY_LOG, SCHEDULE
    private final CalendarCategory category;  // OUTING, VISIT, SUPPLY, EVENT, ETC
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public CareCalendarResponse(CareCalendar calendar) {
        this.id = calendar.getId();
        this.patientId = calendar.getPatient().getId();
        this.writerName = calendar.getWriter().getName();
        this.assigneeName = calendar.getAssignee() != null ? calendar.getAssignee().getName() : null;
        this.dailyLogId = calendar.getDailyLog() != null ? calendar.getDailyLog().getId() : null;
        this.title = calendar.getTitle();
        this.content = calendar.getContent();
        this.calendarType = calendar.getCalendarType();
        this.category = calendar.getCategory();
        this.startTime = calendar.getStartTime();
        this.endTime = calendar.getEndTime();
    }
}