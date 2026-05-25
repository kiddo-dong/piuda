package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;

@Getter
public class MemoryGalleryUploadRequest {
    private String imageUrl;
    private String memo;
}
