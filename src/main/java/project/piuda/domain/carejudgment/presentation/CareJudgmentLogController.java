package project.piuda.domain.carejudgment.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.carejudgment.application.CareJudgmentLogService;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogListResponse;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogRequest;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogResponse;
import project.piuda.domain.carejudgment.domain.JudgmentCategory;

@Tag(name = "Care Judgment Log", description = "케어 판단 기록 (간병인 단독 판단 보관소)")
@RestController
@RequestMapping("/api/v1/patients/{patientId}/care-judgments")
@RequiredArgsConstructor
public class CareJudgmentLogController {

    private final CareJudgmentLogService judgmentLogService;

    @Operation(summary = "판단 기록 작성", description = "간병인만 작성 가능. urgency=IMMEDIATE면 보호자에게 FCM 푸시 전송.")
    @PostMapping
    public ResponseEntity<Long> createLog(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareJudgmentLogRequest request) {
        return ResponseEntity.ok(judgmentLogService.createLog(patientId, userDetails.getUsername(), request));
    }

    @Operation(summary = "판단 기록 보관소 조회", description = "환자에 연결된 사용자(보호자·간병인) 조회. category로 필터링 가능. 즉시공유 건수 포함.")
    @GetMapping
    public ResponseEntity<CareJudgmentLogListResponse> getLogs(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "카테고리 필터 (MEAL/MOVEMENT/MEDICATION). 미지정 시 전체") @RequestParam(required = false) JudgmentCategory category,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(judgmentLogService.getLogs(patientId, category, userDetails.getUsername()));
    }

    @Operation(summary = "판단 기록 단건 조회")
    @GetMapping("/{logId}")
    public ResponseEntity<CareJudgmentLogResponse> getLog(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "기록 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(judgmentLogService.getLog(patientId, logId, userDetails.getUsername()));
    }

    @Operation(summary = "판단 기록 수정", description = "작성한 간병인 본인만 가능.")
    @PutMapping("/{logId}")
    public ResponseEntity<Void> updateLog(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "기록 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareJudgmentLogRequest request) {
        judgmentLogService.updateLog(patientId, logId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "판단 기록 삭제", description = "작성한 간병인 본인만 가능.")
    @DeleteMapping("/{logId}")
    public ResponseEntity<Void> deleteLog(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "기록 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        judgmentLogService.deleteLog(patientId, logId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
