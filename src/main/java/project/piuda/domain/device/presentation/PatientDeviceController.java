package project.piuda.domain.device.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.device.application.DeviceService;
import project.piuda.domain.device.application.dto.DeviceLinkRequest;
import project.piuda.domain.device.application.dto.DeviceResponse;

@Tag(name = "Device", description = "IoT 디바이스 API")
@RestController
@RequestMapping("/api/v1/patients/{patientId}/device")
@RequiredArgsConstructor
public class PatientDeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "환자에 연결된 기기 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "연결된 기기 없음")
    })
    @GetMapping
    public ResponseEntity<DeviceResponse> getDevice(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(deviceService.getDeviceByPatient(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "환자에 기기 연동", description = "등록된 기기 시리얼을 환자에 연결합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연동 성공"),
            @ApiResponse(responseCode = "404", description = "기기 또는 환자 없음")
    })
    @PatchMapping
    public ResponseEntity<Void> linkDevice(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody DeviceLinkRequest request) {
        deviceService.linkDevice(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "환자에서 기기 연동 해제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "해제 성공"),
            @ApiResponse(responseCode = "404", description = "연결된 기기 없음")
    })
    @DeleteMapping
    public ResponseEntity<Void> unlinkDevice(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        deviceService.unlinkDevice(patientId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
