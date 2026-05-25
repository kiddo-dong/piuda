package project.piuda.domain.device.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.device.application.DeviceService;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    // 관리자 또는 공장에서 디바이스 기본 마스터 정보 주입용 API
    @PostMapping
    public ResponseEntity<Long> registerDevice(@RequestBody DeviceRegisterRequest request) {
        return ResponseEntity.ok(deviceService.registerDevice(request));
    }

    // ESP32 마이크가 환자 응답 녹음 후 전송하는 엔드포인트 (JWT 인증 없음)
    @PostMapping("/{deviceSerial}/voice")
    public ResponseEntity<Void> uploadVoice(
            @PathVariable String deviceSerial,
            @RequestParam("audio") MultipartFile audioFile) throws IOException {

        deviceService.saveVoiceRecord(deviceSerial, audioFile);
        return ResponseEntity.ok().build();
    }
}
