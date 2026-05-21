package project.piuda.domain.dailylog.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.dailylog.application.DailyLogService;
import project.piuda.domain.dailylog.application.dto.DailyLogRequest;
import project.piuda.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/daily_logs")
@RequiredArgsConstructor
public class DailyLogController {

    private final DailyLogService dailyLogService;

    @PostMapping
    public ResponseEntity<Long> createDailyLog(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody DailyLogRequest request) {

        Long logId = dailyLogService.createDailyLog(patientId, userDetails.getId(), request);
        return ResponseEntity.ok(logId);
    }
}