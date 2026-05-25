package project.piuda.domain.dailylog.application.dto;

import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.HealthTrend;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class DailyLogResponse {
    private final Long id;
    private final Long patientId;
    private final String writerName;
    private final String writerRole;
    private final LocalDate logDate;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final boolean physicalHygiene;
    private final boolean physicalBath;
    private final boolean physicalMealHelp;
    private final boolean physicalPositionChange;
    private final boolean physicalMobilityHelp;
    private final boolean physicalToiletHelp;
    private final int physicalTotalMinutes;
    private final int cognitiveStimulationMinutes;
    private final int cognitiveLifeTogetherMinutes;
    private final int cognitiveBehaviorManagementMinutes;
    private final int emotionalCommunicationMinutes;
    private final boolean householdMealClean;
    private final boolean householdPersonalHelp;
    private final int householdTotalMinutes;
    private final HealthTrend physicalFunctionTrend;
    private final HealthTrend mealFunctionTrend;
    private final int bowelIncontinenceCount;
    private final int urineIncontinenceCount;
    private final String specialNotes;
    private final String imageUrl;

    public DailyLogResponse(DailyLog log) {
        this.id = log.getId();
        this.patientId = log.getPatient().getId();
        this.writerName = log.getWriter().getName();
        this.writerRole = log.getWriter().getRole().name(); // CAREGIVER, PROTECTOR 등
        this.logDate = log.getLogDate();
        this.startTime = log.getStartTime();
        this.endTime = log.getEndTime();
        this.physicalHygiene = log.isPhysicalHygiene();
        this.physicalBath = log.isPhysicalBath();
        this.physicalMealHelp = log.isPhysicalMealHelp();
        this.physicalPositionChange = log.isPhysicalPositionChange();
        this.physicalMobilityHelp = log.isPhysicalMobilityHelp();
        this.physicalToiletHelp = log.isPhysicalToiletHelp();
        this.physicalTotalMinutes = log.getPhysicalTotalMinutes();
        this.cognitiveStimulationMinutes = log.getCognitiveStimulationMinutes();
        this.cognitiveLifeTogetherMinutes = log.getCognitiveLifeTogetherMinutes();
        this.cognitiveBehaviorManagementMinutes = log.getCognitiveBehaviorManagementMinutes();
        this.emotionalCommunicationMinutes = log.getEmotionalCommunicationMinutes();
        this.householdMealClean = log.isHouseholdMealClean();
        this.householdPersonalHelp = log.isHouseholdPersonalHelp();
        this.householdTotalMinutes = log.getHouseholdTotalMinutes();
        this.physicalFunctionTrend = log.getPhysicalFunctionTrend();
        this.mealFunctionTrend = log.getMealFunctionTrend();
        this.bowelIncontinenceCount = log.getBowelIncontinenceCount();
        this.urineIncontinenceCount = log.getUrineIncontinenceCount();
        this.specialNotes = log.getSpecialNotes();
        this.imageUrl = log.getImageUrl();
    }
}