package project.piuda.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Auth", description = "소셜 로그인 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Google 소셜 로그인", description = "Google ID Token으로 로그인합니다. 신규 사용자는 온보딩 필요.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰 반환")
    @PostMapping("/google")
    public ResponseEntity<SocialLoginResponse> googleLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithGoogle(request.getToken()));
    }

    @Operation(summary = "Kakao 소셜 로그인", description = "Kakao Access Token으로 로그인합니다. 신규 사용자는 온보딩 필요.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰 반환")
    @PostMapping("/kakao")
    public ResponseEntity<SocialLoginResponse> kakaoLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithKakao(request.getToken()));
    }

    @Operation(summary = "Line 소셜 로그인", description = "Line ID Token으로 로그인합니다. 신규 사용자는 온보딩 필요.")
    @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰 반환")
    @PostMapping("/line")
    public ResponseEntity<SocialLoginResponse> lineLogin(@Valid @RequestBody SocialLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithLine(request.getToken()));
    }
}
