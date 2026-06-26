package project.piuda.domain.aireport.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.aireport.application.AiReportService;
import project.piuda.domain.aireport.application.dto.AiReportResponse;

import java.util.List;

@Tag(name = "AI Report", description = "AI 주간 돌봄 리포트")
@RestController
@RequestMapping("/api/v1/patients/{patientId}/ai-reports")
@RequiredArgsConstructor
public class AiReportController {

    private final AiReportService aiReportService;

    @Operation(summary = "주간 리포트 생성", description = "이번 주 일지 데이터를 기반으로 AI 리포트를 생성합니다. 이번 주에 이미 생성된 경우 오류를 반환합니다.")
    @PostMapping
    public ResponseEntity<AiReportResponse> generateReport(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(aiReportService.generateReport(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "리포트 목록 조회", description = "생성된 모든 주간 리포트를 최신순으로 반환합니다.")
    @GetMapping
    public ResponseEntity<List<AiReportResponse>> getReports(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(aiReportService.getReports(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "최신 리포트 조회", description = "가장 최근에 생성된 리포트 1건을 반환합니다.")
    @GetMapping("/latest")
    public ResponseEntity<AiReportResponse> getLatestReport(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(aiReportService.getLatestReport(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "리포트 단건 조회", description = "특정 리포트를 조회합니다.")
    @GetMapping("/{reportId}")
    public ResponseEntity<AiReportResponse> getReport(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "리포트 ID") @PathVariable Long reportId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(aiReportService.getReport(patientId, reportId, userDetails.getUsername()));
    }

    @Operation(summary = "리포트 삭제", description = "특정 리포트를 삭제합니다.")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "리포트 ID") @PathVariable Long reportId,
            @AuthenticationPrincipal UserDetails userDetails) {
        aiReportService.deleteReport(patientId, reportId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
