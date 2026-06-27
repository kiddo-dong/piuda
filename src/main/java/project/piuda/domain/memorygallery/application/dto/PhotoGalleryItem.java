package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PhotoGalleryItem {

    private final Long galleryId;
    private final String imageUrl;
    private final LocalDateTime recordedAt;
    private final String writerName;
    private final String memo;
    private final String source;       // "GALLERY"

    private PhotoGalleryItem(Long galleryId, String imageUrl, LocalDateTime recordedAt,
                              String writerName, String memo, String source) {
        this.galleryId = galleryId;
        this.imageUrl = imageUrl;
        this.recordedAt = recordedAt;
        this.writerName = writerName;
        this.memo = memo;
        this.source = source;
    }

    public static PhotoGalleryItem ofGalleryImage(Long galleryId, String imageUrl, LocalDateTime recordedAt,
                                                   String writerName, String memo) {
        return new PhotoGalleryItem(galleryId, imageUrl, recordedAt, writerName, memo, "GALLERY");
    }
}
