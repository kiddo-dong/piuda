package project.piuda.domain.calendar.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import project.piuda.domain.calendar.domain.CalendarCategory;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CareCalendarRequest {

    @NotBlank(message = "일정 제목을 입력해주세요.")
    private String title;

    private String content;

    private Long assigneeId;

    @NotNull(message = "카테고리를 선택해주세요.")
    private CalendarCategory category;

    @NotNull(message = "시작 시간을 입력해주세요.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간을 입력해주세요.")
    private LocalDateTime endTime;
}
