package project.piuda.domain.calendar.presentation;

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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CareCalendarController {

    private final CareCalendarService careCalendarService;

    // 1. 수동 일정 등록
    @PostMapping("/patients/{patientId}/calendars")
    public ResponseEntity<Long> createSchedule(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareCalendarRequest request) {
        Long id = careCalendarService.createSchedule(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok(id);
    }

    // 2. 환자의 캘린더 전체 일정 조회
    @GetMapping("/patients/{patientId}/calendars")
    public ResponseEntity<List<CareCalendarResponse>> getCalendarEvents(@PathVariable Long patientId) {
        return ResponseEntity.ok(careCalendarService.getCalendarEvents(patientId));
    }

    // 3. 일정 수정
    @PutMapping("/calendars/{calendarId}")
    public ResponseEntity<Void> updateSchedule(
            @PathVariable Long calendarId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareCalendarRequest request) {
        careCalendarService.updateSchedule(calendarId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    // 4. 일정 삭제
    @DeleteMapping("/calendars/{calendarId}")
    public ResponseEntity<Void> deleteCalendarEvent(
            @PathVariable Long calendarId,
            @AuthenticationPrincipal UserDetails userDetails) {
        careCalendarService.deleteCalendarEvent(calendarId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}