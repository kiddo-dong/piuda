package project.piuda.domain.dailylog.presentation;

import project.piuda.domain.dailylog.application.DailyLogService;
import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    // 1. 하루 일지 작성 (작성 즉시 달력 도장 연동)
    @PostMapping(value = "/patients/{patientId}/daily-logs", consumes = "multipart/form-data")
    public ResponseEntity<Long> createDailyLog(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") DailyLogRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        Long logId = dailyLogService.createDailyLog(patientId, userDetails.getUsername(), request, image);
        return ResponseEntity.ok(logId);
    }

    // 2. 특정 환자의 하루 일지 목록 조회
    @GetMapping("/patients/{patientId}/daily-logs")
    public ResponseEntity<List<DailyLogResponse>> getDailyLogs(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dailyLogService.getDailyLogs(patientId, userDetails.getUsername()));
    }

    // 3. 하루 일지 상세 단건 조회
    @GetMapping("/daily-logs/{logId}")
    public ResponseEntity<DailyLogResponse> getDailyLogDetails(
            @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(dailyLogService.getDailyLogDetails(logId, userDetails.getUsername()));
    }

    // 4. 하루 일지 수정
    @PutMapping(value = "/daily-logs/{logId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateDailyLog(
            @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") DailyLogRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {

        dailyLogService.updateDailyLog(logId, userDetails.getUsername(), request, image);
        return ResponseEntity.ok().build();
    }

    // 5. 하루 일지 삭제
    @DeleteMapping("/daily-logs/{logId}")
    public ResponseEntity<Void> deleteDailyLog(
            @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails) {
        dailyLogService.deleteDailyLog(logId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}