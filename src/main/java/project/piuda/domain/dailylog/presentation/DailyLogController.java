package project.piuda.domain.dailylog.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.piuda.domain.dailylog.application.DailyLogService;
import project.piuda.domain.dailylog.application.dto.DailyLogCreateRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogUpdateRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "DailyLog", description = "하루 일지 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    @Operation(summary = "하루 일지 작성", description = "일지 작성 시 케어 캘린더에 DAILY_LOG 타입 항목이 자동 생성됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공 - 일지 ID 반환"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PostMapping(value = "/patients/{patientId}/daily-logs", consumes = "multipart/form-data")
    public ResponseEntity<Long> createDailyLog(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") DailyLogCreateRequest request,
            @Parameter(description = "첨부 이미지 (선택)") @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Long logId = dailyLogService.createDailyLog(patientId, userDetails.getUsername(), request, image);
        return ResponseEntity.ok(logId);
    }

    @Operation(summary = "환자 하루 일지 목록 조회", description = "같은 환자에 연결된 모든 사용자가 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/patients/{patientId}/daily-logs")
    public ResponseEntity<List<DailyLogResponse>> getDailyLogs(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dailyLogService.getDailyLogs(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "하루 일지 단건 조회", description = "일지 상세 내용을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "일지 없음")
    })
    @GetMapping("/daily-logs/{logId}")
    public ResponseEntity<DailyLogResponse> getDailyLogDetails(
            @Parameter(description = "일지 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dailyLogService.getDailyLogDetails(logId, userDetails.getUsername()));
    }

    @Operation(summary = "하루 일지 수정", description = "본인이 작성한 일지만 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일지 없음")
    })
    @PutMapping(value = "/daily-logs/{logId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateDailyLog(
            @Parameter(description = "일지 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") DailyLogUpdateRequest request,
            @Parameter(description = "새 첨부 이미지 (선택)") @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        dailyLogService.updateDailyLog(logId, userDetails.getUsername(), request, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "하루 일지 삭제", description = "본인이 작성한 일지만 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일지 없음")
    })
    @DeleteMapping("/daily-logs/{logId}")
    public ResponseEntity<Void> deleteDailyLog(
            @Parameter(description = "일지 ID") @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        dailyLogService.deleteDailyLog(logId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
