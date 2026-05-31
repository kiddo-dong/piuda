package project.piuda.domain.dailylog.application.dto;

import project.piuda.domain.dailylog.domain.HealthTrend;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class DailyLogCreateRequest {
    private LocalDate logDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private boolean physicalHygiene;
    private boolean physicalBath;
    private boolean physicalMealHelp;
    private boolean physicalPositionChange;
    private boolean physicalMobilityHelp;
    private boolean physicalToiletHelp;
    private int physicalTotalMinutes;

    private int cognitiveStimulationMinutes;
    private int cognitiveLifeTogetherMinutes;
    private int cognitiveBehaviorManagementMinutes;

    private int emotionalCommunicationMinutes;

    private boolean householdMealClean;
    private boolean householdPersonalHelp;
    private int householdTotalMinutes;

    private HealthTrend physicalFunctionTrend;
    private HealthTrend mealFunctionTrend;
    private int bowelIncontinenceCount;
    private int urineIncontinenceCount;

    private String specialNotes;
}
