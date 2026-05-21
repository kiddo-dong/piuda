package project.piuda.domain.device.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.device.application.DeviceService;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;

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
}