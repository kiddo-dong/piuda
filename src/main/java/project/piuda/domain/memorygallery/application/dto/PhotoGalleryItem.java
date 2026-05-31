package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PhotoGalleryItem {

    private final Long galleryId;   // 직접 등록 사진만 존재, 일지 사진은 null
    private final String imageUrl;
    private final LocalDateTime recordedAt;
    private final String writerNickname;

    private PhotoGalleryItem(Long galleryId, String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        this.galleryId = galleryId;
        this.imageUrl = imageUrl;
        this.recordedAt = recordedAt;
        this.writerNickname = writerNickname;
    }

    public static PhotoGalleryItem ofGalleryImage(Long galleryId, String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new PhotoGalleryItem(galleryId, imageUrl, recordedAt, writerNickname);
    }

    public static PhotoGalleryItem ofDailyLogImage(String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new PhotoGalleryItem(null, imageUrl, recordedAt, writerNickname);
    }
}
