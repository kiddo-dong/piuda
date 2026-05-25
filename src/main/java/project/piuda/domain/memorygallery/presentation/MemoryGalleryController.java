package project.piuda.domain.memorygallery.presentation;

import project.piuda.domain.memorygallery.application.MemoryGalleryService;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryItem;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryUploadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/gallery")
@RequiredArgsConstructor
public class MemoryGalleryController {

    private final MemoryGalleryService memoryGalleryService;

    @PostMapping
    public ResponseEntity<Void> uploadPhoto(
            @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MemoryGalleryUploadRequest request) {
        memoryGalleryService.uploadPhoto(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<MemoryGalleryItem>> getGallery(@PathVariable Long patientId) {
        return ResponseEntity.ok(memoryGalleryService.getGallery(patientId));
    }
}
