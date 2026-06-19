package project.piuda.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.user.application.UserService;
import project.piuda.domain.user.application.dto.*;
import project.piuda.domain.user.application.dto.FcmTokenRequest;
import project.piuda.domain.user.application.dto.PublicUserResponse;

import java.io.IOException;
import java.util.List;

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @Operation(summary = "회원가입", description = "일반 회원가입입니다. multipart/form-data로 전송하며 프로필 이미지는 선택입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패 (비밀번호 규칙, 닉네임 중복 등)")
    })
    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<Void> signUp(
            @Valid @RequestPart("data") SignUpRequest request,
            @Parameter(description = "프로필 이미지 (선택)") @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        service.signUp(request, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인합니다. JWT 토큰을 반환합니다. 인증 불필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공 - JWT 토큰 반환"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호 불일치")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(service.getMe(userDetails.getUsername()));
    }

    @Operation(summary = "내 정보 수정", description = "프로필 정보를 수정합니다. multipart/form-data로 전송합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @PutMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") UserUpdateRequest request,
            @Parameter(description = "새 프로필 이미지 (선택)") @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        service.updateMe(userDetails.getUsername(), request, image);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "계정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "탈퇴 성공")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserDetails userDetails) {
        service.deleteMe(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "액세스 토큰 재발급",
            description = """
                    리프레시 토큰으로 새 액세스/리프레시 토큰을 발급받습니다. 인증 불필요.

                    **Request Body (application/json)**
                    ```json
                    { "refreshToken": "<발급받은 리프레시 토큰>" }
                    ```

                    - Token Rotation 방식: 호출마다 리프레시 토큰도 새로 발급됩니다.
                    - 이전 리프레시 토큰은 즉시 무효화되므로 응답의 새 토큰으로 교체 저장하세요.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공 — 새 accessToken, refreshToken 반환"),
            @ApiResponse(responseCode = "403", description = "유효하지 않거나 만료된 리프레시 토큰")
    })
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(service.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        service.logout(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "소셜 로그인 온보딩", description = "소셜 로그인 신규 사용자가 닉네임, 역할 등 추가 정보를 입력합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "온보딩 완료 - JWT 토큰 반환"),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패")
    })
    @PostMapping("/onboarding")
    public ResponseEntity<TokenResponse> completeOnboarding(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(service.completeOnboarding(userDetails.getUsername(), request));
    }

    @Operation(summary = "닉네임 중복 확인", description = "닉네임 사용 가능 여부를 확인합니다. true이면 사용 가능.")
    @ApiResponse(responseCode = "200", description = "확인 성공 - true: 사용 가능, false: 중복")
    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(
            @Parameter(description = "확인할 닉네임") @RequestParam String nickname) {
        return ResponseEntity.ok(service.checkNickname(nickname));
    }

    @Operation(summary = "내공점수 랭킹 조회", description = "내공점수 상위 사용자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking(
            @Parameter(description = "조회할 상위 인원 수 (기본값 10)") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getRanking(limit));
    }

    @Operation(summary = "FCM 토큰 등록/갱신", description = "앱 시작 시 FCM 디바이스 토큰을 서버에 등록합니다. 푸쉬 알림 수신에 필요합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @PutMapping("/fcm-token")
    public ResponseEntity<Void> updateFcmToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody FcmTokenRequest request) {
        service.updateFcmToken(userDetails.getUsername(), request.getFcmToken());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "공개 프로필 조회", description = "닉네임으로 다른 사용자의 공개 프로필을 조회합니다. 인증 필요.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 닉네임")
    })
    @GetMapping("/{nickname}/profile")
    public ResponseEntity<PublicUserResponse> getUserProfile(
            @Parameter(description = "조회할 사용자 닉네임") @PathVariable String nickname) {
        return ResponseEntity.ok(service.getUserProfile(nickname));
    }
}
