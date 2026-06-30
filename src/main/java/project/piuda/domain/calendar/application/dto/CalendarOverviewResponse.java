package project.piuda.domain.calendar.application.dto;

import lombok.Getter;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogResponse;

import java.util.List;

/**
 * 캘린더 통합 조회 응답.
 * 저장은 분리돼 있지만, 조회 시 해당 환자의 수동 일정(calendars)과
 * 케어 판단 기록(judgmentLogs)을 함께 묶어 반환한다. (판단 기록은 createdAt 기준으로 날짜에 매핑)
 */
@Getter
public class CalendarOverviewResponse {

    private final List<CareCalendarResponse> calendars;
    private final List<CareJudgmentLogResponse> judgmentLogs;

    public CalendarOverviewResponse(List<CareCalendarResponse> calendars,
                                    List<CareJudgmentLogResponse> judgmentLogs) {
        this.calendars = calendars;
        this.judgmentLogs = judgmentLogs;
    }
}
