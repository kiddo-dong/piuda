package project.piuda.domain.report.application.dto;

import lombok.Getter;
import project.piuda.domain.report.domain.Report;
import project.piuda.domain.report.domain.ReportReason;
import project.piuda.domain.report.domain.ReportStatus;
import project.piuda.domain.report.domain.ReportTargetType;

import java.time.LocalDateTime;

@Getter
public class AdminReportResponse {
    private final Long reportId;
    private final String reporterNickname;
    private final ReportTargetType targetType;
    private final Long targetId;
    private final ReportReason reason;
    private final ReportStatus status;
    private final LocalDateTime createdAt;

    public AdminReportResponse(Report report) {
        this.reportId = report.getId();
        this.reporterNickname = report.getReporter().getNickname();
        this.targetType = report.getTargetType();
        this.targetId = report.getTargetId();
        this.reason = report.getReason();
        this.status = report.getStatus();
        this.createdAt = report.getCreatedAt();
    }
}
