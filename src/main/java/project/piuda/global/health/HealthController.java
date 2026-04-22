package project.piuda.global.health;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.piuda.global.dto.ApiResponse;

@RestController
@RequestMapping("/health")
@RequiredArgsConstructor
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("API Health OK"));
    }

}