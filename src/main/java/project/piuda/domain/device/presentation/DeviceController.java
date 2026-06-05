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
import project.piuda.domain.device.application.DeviceService;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;

import java.io.IOException;

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
}
