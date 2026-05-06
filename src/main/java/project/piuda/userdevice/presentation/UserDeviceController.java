package project.piuda.userdevice.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.device.application.dto.DeviceResponse;
import project.piuda.global.dto.ApiResponse;
import project.piuda.global.security.principal.UserPrincipal;
import project.piuda.userdevice.application.service.UserDeviceService;

import java.util.List;

@RestController
@RequestMapping("/api/user/devices")
@RequiredArgsConstructor
public class UserDeviceController {

    private final UserDeviceService userDeviceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getMyDevices(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(ApiResponse.success(userDeviceService.getMyDevices(user.id())));
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<DeviceResponse>> getMyDevice(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long deviceId
    ) {
        return ResponseEntity.ok(ApiResponse.success(userDeviceService.getMyDevice(user.id(), deviceId)));
    }

    @DeleteMapping("/{deviceId}")
    public ResponseEntity<ApiResponse<String>> disconnect(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long deviceId
    ) {
        userDeviceService.disconnect(user.id(), deviceId);
        return ResponseEntity.ok(ApiResponse.success("디바이스 연결 해제 완료"));
    }
}
