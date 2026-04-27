package project.piuda.auth.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.auth.application.service.AuthService;
import project.piuda.global.dto.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> req) {

        String token = authService.login(
                req.get("email"),
                req.get("password")
        );

        return ResponseEntity.ok(ApiResponse.success(token));
    }
}