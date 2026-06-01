package project.piuda.domain.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.user.application.UserService;
import project.piuda.domain.user.application.dto.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping(value = "/signup", consumes = "multipart/form-data")
    public ResponseEntity<Void> signUp(
            @Valid @RequestPart("data") SignUpRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        service.signUp(request, image);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(service.getMe(userDetails.getUsername()));
    }

    @PutMapping(value = "/me", consumes = "multipart/form-data")
    public ResponseEntity<Void> updateMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("data") UserUpdateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        service.updateMe(userDetails.getUsername(), request, image);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal UserDetails userDetails) {
        service.deleteMe(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/onboarding")
    public ResponseEntity<TokenResponse> completeOnboarding(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OnboardingRequest request) {
        return ResponseEntity.ok(service.completeOnboarding(userDetails.getUsername(), request));
    }

    @GetMapping("/check-nickname")
    public ResponseEntity<Boolean> checkNickname(@RequestParam String nickname) {
        return ResponseEntity.ok(service.checkNickname(nickname));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getRanking(limit));
    }
}
