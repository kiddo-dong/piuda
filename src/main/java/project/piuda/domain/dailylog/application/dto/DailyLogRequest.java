package project.piuda.domain.dailylog.application.dto;

import project.piuda.domain.dailylog.domain.DailyStatus; // 깔끔한 임포트
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DailyLogRequest {
    private LocalDate logDate;
    private DailyStatus mealStatus;      // 식사 상태 (GOOD, NORMAL, POOR)
    private boolean medicationStatus;
    private DailyStatus sleepStatus;     // 수면 상태 (GOOD, NORMAL, POOR)
    private boolean walkStatus;
    private String abnormalBehavior;
    private String generalNote;
}