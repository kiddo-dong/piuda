package project.piuda.domain.calendar.application.dto;

import project.piuda.domain.calendar.domain.CalendarCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CareCalendarRequest {
    private String title;
    private String content;
    private Long assigneeId; // 역할을 분담할 담당 가족/간병인의 User ID (Optional)
    private CalendarCategory category; // OUTING, VISIT, SUPPLY, EVENT, ETC
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}