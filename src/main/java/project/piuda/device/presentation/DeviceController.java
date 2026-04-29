package project.piuda.device.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.device.application.service.DeviceService;
import project.piuda.global.dto.ApiResponse;
import project.piuda.global.security.principal.UserPrincipal;
import project.piuda.userdevice.application.service.UserDeviceService;

import java.util.Map;

@RestController
@RequestMapping("/api/device")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final UserDeviceService userDeviceService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@AuthenticationPrincipal UserPrincipal user,
                                                        @RequestBody Map<String, String> req) {

        String deviceId = req.get("deviceId");
        String key = deviceService.register(deviceId);
        userDeviceService.connect(user.id(), deviceId);

        return ResponseEntity.ok(ApiResponse.success(key));
    }
}
