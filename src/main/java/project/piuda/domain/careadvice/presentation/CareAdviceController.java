package project.piuda.domain.careadvice.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.careadvice.application.CareAdviceService;
import project.piuda.domain.careadvice.application.dto.*;

import java.util.List;

@Tag(name = "CareAdvice", description = "AI 케어 어드바이스 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CareAdviceController {

    private final CareAdviceService careAdviceService;

    @Operation(summary = "새 대화 세션 시작", description = "환자 정보를 기반으로 AI 케어 어드바이스 대화 세션을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "세션 생성 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PostMapping("/patients/{patientId}/care-advice/sessions")
    public ResponseEntity<CareAdviceSessionResponse> createSession(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(careAdviceService.createSession(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "메시지 전송 및 AI 응답 수신", description = "세션에 메시지를 보내고 AI 응답을 받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 응답 반환"),
            @ApiResponse(responseCode = "404", description = "세션 없음")
    })
    @PostMapping("/care-advice/sessions/{sessionId}/messages")
    public ResponseEntity<SendCareAdviceResponse> sendMessage(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CareAdviceMessageRequest request) {
        return ResponseEntity.ok(careAdviceService.sendMessage(sessionId, userDetails.getUsername(), request));
    }

    @Operation(summary = "세션 삭제", description = "AI 케어 어드바이스 대화 세션을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "세션 없음")
    })
    @DeleteMapping("/care-advice/sessions/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        careAdviceService.deleteSession(sessionId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "세션 목록 조회", description = "환자의 케어 어드바이스 세션 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/patients/{patientId}/care-advice/sessions")
    public ResponseEntity<List<CareAdviceSessionResponse>> getSessions(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(careAdviceService.getSessions(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "세션 메시지 전체 조회", description = "특정 세션의 전체 대화 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/care-advice/sessions/{sessionId}/messages")
    public ResponseEntity<List<CareAdviceMessageResponse>> getMessages(
            @Parameter(description = "세션 ID") @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(careAdviceService.getMessages(sessionId, userDetails.getUsername()));
    }
}
