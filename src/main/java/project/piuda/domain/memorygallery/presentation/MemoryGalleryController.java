package project.piuda.domain.memorygallery.presentation;

import project.piuda.domain.memorygallery.application.MemoryGalleryService;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryItem;
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "memo", required = false) String memo) throws IOException {
        memoryGalleryService.uploadPhoto(patientId, userDetails.getUsername(), image, memo);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MemoryGalleryItem>> getGallery(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(memoryGalleryService.getGallery(patientId, userDetails.getUsername()));
    }

    @DeleteMapping("/{galleryId}")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long patientId,
            @PathVariable Long galleryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        memoryGalleryService.deletePhoto(galleryId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}