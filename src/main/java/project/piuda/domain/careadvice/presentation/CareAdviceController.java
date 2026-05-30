package project.piuda.domain.careadvice.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.careadvice.application.CareAdviceService;
import project.piuda.domain.careadvice.application.dto.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CareAdviceController {

    private final CareAdviceService careAdviceService;

    // 새 대화 세션 시작
    @PostMapping("/patients/{patientId}/care-advice/sessions")
    public ResponseEntity<CareAdviceSessionResponse> createSession(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(careAdviceService.createSession(patientId, userDetails.getUsername()));
    }

    // 메시지 전송 + AI 응답 수신
    @PostMapping("/care-advice/sessions/{sessionId}/messages")
    public ResponseEntity<SendCareAdviceResponse> sendMessage(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CareAdviceMessageRequest request) {

        return ResponseEntity.ok(careAdviceService.sendMessage(sessionId, userDetails.getUsername(), request));
    }

    // 세션 삭제
    @DeleteMapping("/care-advice/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        careAdviceService.deleteSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 세션 목록 조회
    @GetMapping("/patients/{patientId}/care-advice/sessions")
    public ResponseEntity<List<CareAdviceSessionResponse>> getSessions(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(careAdviceService.getSessions(patientId, userDetails.getUsername()));
    }

    // 특정 세션의 전체 메시지 조회
    @GetMapping("/care-advice/sessions/{sessionId}/messages")
    public ResponseEntity<List<CareAdviceMessageResponse>> getMessages(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(careAdviceService.getMessages(sessionId, userDetails.getUsername()));
    }
}
