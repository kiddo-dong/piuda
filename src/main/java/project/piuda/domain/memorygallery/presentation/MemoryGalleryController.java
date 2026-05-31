package project.piuda.domain.memorygallery.presentation;

import project.piuda.domain.memorygallery.application.MemoryGalleryService;
import project.piuda.domain.memorygallery.application.dto.AudioGalleryItem;
import project.piuda.domain.memorygallery.application.dto.PhotoGalleryItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/gallery")
@RequiredArgsConstructor
public class MemoryGalleryController {

    private final MemoryGalleryService memoryGalleryService;

    // 사진 갤러리 업로드
    @PostMapping(value = "/photos", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "memo", required = false) String memo) throws IOException {
        memoryGalleryService.uploadPhoto(patientId, userDetails.getUsername(), image, memo);
        return ResponseEntity.ok().build();
    }

    // 사진 갤러리 조회 (직접 올린 사진 + 일지 첨부 사진)
    @GetMapping("/photos")
    public ResponseEntity<List<PhotoGalleryItem>> getPhotoGallery(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memoryGalleryService.getPhotoGallery(patientId, userDetails.getUsername()));
    }

    // 사진 삭제 (직접 올린 사진만 가능, galleryId 필요)
    @DeleteMapping("/photos/{galleryId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long patientId,
            @PathVariable Long galleryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        memoryGalleryService.deletePhoto(galleryId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 음성 갤러리 조회
    @GetMapping("/audio")
    public ResponseEntity<List<AudioGalleryItem>> getAudioGallery(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memoryGalleryService.getAudioGallery(patientId, userDetails.getUsername()));
    }

    // 음성 삭제
    @DeleteMapping("/audio/{audioId}")
    public ResponseEntity<Void> deleteAudio(
            @PathVariable Long patientId,
            @PathVariable Long audioId,
            @AuthenticationPrincipal UserDetails userDetails) {
        memoryGalleryService.deleteAudio(audioId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
