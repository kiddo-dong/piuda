package project.piuda.domain.device.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import project.piuda.domain.device.application.DeviceService;
import project.piuda.domain.device.application.dto.*;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "Device", description = "IoT 디바이스 API")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "디바이스 등록", description = "ESP32 디바이스 마스터 정보를 등록합니다. 인증 불필요.")
    @ApiResponse(responseCode = "200", description = "등록 성공 - 디바이스 ID 반환")
    @PostMapping
    public ResponseEntity<Long> registerDevice(@RequestBody DeviceRegisterRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }

    @Operation(summary = "기기 삭제", description = "기기를 삭제합니다. 연결된 환자가 있으면 자동으로 해제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "기기 없음")
    })
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "기기 ID") @PathVariable Long deviceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        deviceService.deleteDevice(deviceId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "음성 녹음 파일 업로드", description = "ESP32 마이크가 환자 응답 녹음 후 전송하는 엔드포인트입니다. 인증 불필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "404", description = "디바이스 없음")
    })
    @PostMapping("/{deviceSerial}/voice")
    public ResponseEntity<Void> uploadVoice(
            @Parameter(description = "디바이스 시리얼 번호") @PathVariable String deviceSerial,
            @Parameter(description = "녹음 파일 (audio)") @RequestParam("audio") MultipartFile audioFile) throws IOException {
        deviceService.saveVoiceRecord(deviceSerial, audioFile);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "TTS 메시지 전송 (앱 → 기기)", description = "텍스트를 TTS 변환하여 기기 재생 큐에 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "큐 등록 성공"),
            @ApiResponse(responseCode = "404", description = "디바이스 없음")
    })
    @PostMapping("/{deviceSerial}/tts")
    public ResponseEntity<Void> queueTts(
            @Parameter(description = "디바이스 시리얼 번호") @PathVariable String deviceSerial,
            @RequestBody TtsQueueRequest request) throws IOException {
        deviceService.queueTts(deviceSerial, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "다음 TTS 메시지 조회 (기기 폴링)", description = "ESP32가 주기적으로 호출하여 재생할 TTS 오디오 URL을 받습니다. 인증 불필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재생할 메시지 있음"),
            @ApiResponse(responseCode = "204", description = "재생할 메시지 없음")
    })
    @GetMapping("/{deviceSerial}/tts/next")
    public ResponseEntity<TtsNextResponse> getNextTts(
            @Parameter(description = "디바이스 시리얼 번호") @PathVariable String deviceSerial) {
        Optional<TtsNextResponse> next = deviceService.getNextTts(deviceSerial);
        return next.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.noContent().build());
    }

    @Operation(summary = "TTS 재생 완료 ACK (기기)", description = "ESP32가 재생 완료 후 호출합니다. 인증 불필요.")
    @ApiResponse(responseCode = "200", description = "ACK 성공")
    @PostMapping("/{deviceSerial}/tts/{messageId}/ack")
    public ResponseEntity<Void> ackTts(
            @Parameter(description = "디바이스 시리얼 번호") @PathVariable String deviceSerial,
            @Parameter(description = "메시지 ID") @PathVariable Long messageId) {
        deviceService.ackTts(deviceSerial, messageId);
        return ResponseEntity.ok().build();
    }
}
