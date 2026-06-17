package project.piuda.domain.report.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.report.domain.ReportReason;

@Getter
@NoArgsConstructor
public class ReportRequest {
    private ReportReason reason;
}
