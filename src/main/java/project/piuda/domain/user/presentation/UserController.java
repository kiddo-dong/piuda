package project.piuda.domain.user.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.user.application.UserService;
import project.piuda.domain.user.application.dto.LoginRequest;
import project.piuda.domain.user.application.dto.RankingResponse;
import project.piuda.domain.user.application.dto.SignUpRequest;
import project.piuda.domain.user.application.dto.TokenResponse;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
        service.signUp(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<RankingResponse>> getRanking(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getRanking(limit));
    }
}
