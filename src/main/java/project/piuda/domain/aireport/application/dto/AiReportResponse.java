package project.piuda.domain.aireport.application.dto;

import lombok.Getter;
import project.piuda.domain.aireport.domain.AiReport;
import project.piuda.domain.aireport.domain.CareRecommendation;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AiReportResponse {
    private final Long id;
    private final LocalDate weekStart;
    private final LocalDate weekEnd;
    private final String content;
    private final CareRecommendation recommendation;
    private final LocalDateTime createdAt;

    public AiReportResponse(AiReport report) {
        this.id = report.getId();
        this.weekStart = report.getWeekStart();
        this.weekEnd = report.getWeekStart().plusDays(6);
        this.content = report.getContent();
        this.recommendation = report.getRecommendation();
        this.createdAt = report.getCreatedAt();
    }
}
