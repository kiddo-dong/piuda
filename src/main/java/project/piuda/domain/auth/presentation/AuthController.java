package project.piuda.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.domain.auth.application.AuthService;
import project.piuda.domain.user.application.dto.SocialLoginRequest;
import project.piuda.domain.user.application.dto.SocialLoginResponse;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<SocialLoginResponse> googleLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getToken()));
    }

    @PostMapping("/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithKakao(request.getToken()));
    }

    @PostMapping("/line")
    public ResponseEntity<SocialLoginResponse> lineLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithLine(request.getToken()));
    }
}
