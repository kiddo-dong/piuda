package project.piuda.domain.calendar.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import project.piuda.domain.calendar.application.CareCalendarService;
import project.piuda.domain.calendar.application.dto.CareCalendarRequest;
import project.piuda.domain.calendar.application.dto.CareCalendarResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Calendar", description = "케어 캘린더 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CareCalendarController {

    private final CareCalendarService careCalendarService;

    @Operation(summary = "수동 일정 등록", description = "환자의 케어 캘린더에 직접 일정을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공 - 캘린더 ID 반환"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PostMapping("/patients/{patientId}/calendars")
    public ResponseEntity<Long> createSchedule(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareCalendarRequest request) {
        Long id = careCalendarService.createSchedule(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok(id);
    }

    @Operation(summary = "환자 캘린더 전체 일정 조회", description = "환자의 수동 등록 일정 및 하루일지 자동 생성 일정을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/patients/{patientId}/calendars")
    public ResponseEntity<List<CareCalendarResponse>> getCalendarEvents(
            @Parameter(description = "환자 ID") @PathVariable Long patientId) {
        return ResponseEntity.ok(careCalendarService.getCalendarEvents(patientId));
    }

    @Operation(summary = "일정 수정", description = "MANUAL 타입 일정만 수정 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    @PutMapping("/calendars/{calendarId}")
    public ResponseEntity<Void> updateSchedule(
            @Parameter(description = "캘린더 ID") @PathVariable Long calendarId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareCalendarRequest request) {
        careCalendarService.updateSchedule(calendarId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일정 삭제", description = "MANUAL 타입 일정만 삭제 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일정 없음")
    })
    @DeleteMapping("/calendars/{calendarId}")
    public ResponseEntity<Void> deleteCalendarEvent(
            @Parameter(description = "캘린더 ID") @PathVariable Long calendarId,
            @AuthenticationPrincipal UserDetails userDetails) {
        careCalendarService.deleteCalendarEvent(calendarId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
