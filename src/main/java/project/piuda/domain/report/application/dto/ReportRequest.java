package project.piuda.domain.report.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.report.domain.ReportReason;

@Getter
@NoArgsConstructor
public class ReportRequest {

    @NotNull(message = "신고 사유를 선택해주세요.")
    private ReportReason reason;
}
