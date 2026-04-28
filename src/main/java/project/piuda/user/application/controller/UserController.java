package project.piuda.user.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.global.dto.ApiResponse;
import project.piuda.user.application.service.UserService;
import project.piuda.user.domain.User;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody Map<String, String> request) {

        userService.signup(request.get("email"), request.get("password"));
        return ResponseEntity.ok(ApiResponse.success("회원가입 완료"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody Map<String, String> request) {

        User user = userService.login(request.get("email"), request.get("password"));
        return ResponseEntity.ok(ApiResponse.success("로그인 성공"));
    }
}