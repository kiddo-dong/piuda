package project.piuda.audio.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.piuda.audio.application.AudioService;
import project.piuda.global.dto.ApiResponse;
import project.piuda.global.security.principal.DevicePrincipal;

@RestController
@RequestMapping("/api/device/audio")
@RequiredArgsConstructor
public class AudioController {

    private final AudioService audioService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> upload(@AuthenticationPrincipal DevicePrincipal device) {

        audioService.save(device.id());

        return ResponseEntity.ok(ApiResponse.success("업로드 성공"));
    }
}
