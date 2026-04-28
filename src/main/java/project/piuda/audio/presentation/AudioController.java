package project.piuda.audio.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.piuda.audio.application.AudioService;
import project.piuda.device.domain.Device;
import project.piuda.global.dto.ApiResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/device/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> upload() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Device device = (Device) auth.getPrincipal();

        audioService.save(device);

        return ResponseEntity.ok(ApiResponse.success("업로드 성공"));
    }
}