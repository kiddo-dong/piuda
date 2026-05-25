package project.piuda.domain.dailylog.presentation;

import project.piuda.domain.dailylog.application.DailyLogService;
import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.domain.dailylog.application.dto.DailyLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    // 1. 하루 일지 작성 (작성 즉시 달력 도장 연동)
    @PostMapping("/patients/{patientId}/daily-logs")
    public ResponseEntity<Long> createDailyLog(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DailyLogRequest request) {

        Long logId = dailyLogService.createDailyLog(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok(logId);
    }

    // 2. 특정 환자의 하루 일지 목록 조회
    @GetMapping("/patients/{patientId}/daily-logs")
    public ResponseEntity<List<DailyLogResponse>> getDailyLogs(@PathVariable Long patientId) {
        return ResponseEntity.ok(dailyLogService.getDailyLogs(patientId));
    }

    // 3. 하루 일지 상세 단건 조회
    @GetMapping("/daily-logs/{logId}")
    public ResponseEntity<DailyLogResponse> getDailyLogDetails(@PathVariable Long logId) {
        return ResponseEntity.ok(dailyLogService.getDailyLogDetails(logId));
    }

    // 4. 하루 일지 수정
    @PutMapping("/daily-logs/{logId}")
    public ResponseEntity<Void> updateDailyLog(
            @PathVariable Long logId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody DailyLogRequest request) {

        dailyLogService.updateDailyLog(logId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    // 5. 하루 일지 삭제
    @DeleteMapping("/daily-logs/{logId}")
    public ResponseEntity<Void> deleteDailyLog(@PathVariable Long logId) {
        dailyLogService.deleteDailyLog(logId);
        return ResponseEntity.ok().build();
    }
}