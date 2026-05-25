package project.piuda.global.presentation;

import project.piuda.global.infrastructure.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ImageUploadController {

    private final S3UploadService s3UploadService;

    // Flutter에서 파일 전송 포맷(Multipart)으로 사진을 쏘는 창구
    @PostMapping("/images/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile multipartFile) throws IOException {

        // s3의 'daily-log' 라는 폴더 안에 이미지 저장 후 URL 반환
        String imageUrl = s3UploadService.upload(multipartFile, "daily-log");
        return ResponseEntity.ok(imageUrl);
    }
}