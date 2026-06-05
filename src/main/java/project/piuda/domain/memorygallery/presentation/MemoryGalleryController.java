package project.piuda.domain.memorygallery.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.memorygallery.application.MemoryGalleryService;
import project.piuda.domain.memorygallery.application.dto.AudioGalleryItem;
import project.piuda.domain.memorygallery.application.dto.PhotoGalleryItem;

import java.io.IOException;
import java.util.List;

@Tag(name = "MemoryGallery", description = "환자 기억 갤러리 API")
@RestController
@RequestMapping("/api/v1/patients/{patientId}/gallery")
@RequiredArgsConstructor
public class MemoryGalleryController {

    private final MemoryGalleryService memoryGalleryService;

    @Operation(summary = "사진 업로드", description = "환자 갤러리에 사진을 업로드합니다. S3에 저장됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "업로드 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PostMapping(value = "/photos", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadPhoto(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "업로드할 이미지 파일") @RequestPart("image") MultipartFile image,
            @Parameter(description = "메모 (선택)") @RequestPart(value = "memo", required = false) String memo) throws IOException {
        memoryGalleryService.uploadPhoto(patientId, userDetails.getUsername(), image, memo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사진 갤러리 조회", description = "직접 업로드한 사진과 하루일지 첨부 사진을 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/photos")
    public ResponseEntity<List<PhotoGalleryItem>> getPhotoGallery(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memoryGalleryService.getPhotoGallery(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "사진 삭제", description = "갤러리에서 사진을 삭제합니다. 업로더 본인만 삭제 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사진 없음")
    })
    @DeleteMapping("/photos/{galleryId}")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "갤러리 항목 ID") @PathVariable Long galleryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        memoryGalleryService.deletePhoto(galleryId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "음성 갤러리 조회", description = "IoT 디바이스가 녹음한 환자 음성 기록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/audio")
    public ResponseEntity<List<AudioGalleryItem>> getAudioGallery(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memoryGalleryService.getAudioGallery(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "음성 기록 삭제", description = "음성 기록을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "음성 기록 없음")
    })
    @DeleteMapping("/audio/{audioId}")
    public ResponseEntity<Void> deleteAudio(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "음성 기록 ID") @PathVariable Long audioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        memoryGalleryService.deleteAudio(audioId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
