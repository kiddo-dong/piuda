package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PhotoGalleryItem {

    private final Long galleryId;      // 갤러리 직접 업로드 사진만 존재, 일지 사진은 null (null이면 삭제 불가)
    private final String imageUrl;
    private final LocalDateTime recordedAt;
    private final String writerName;
    private final String memo;         // 갤러리 직접 업로드 사진만 존재, 일지 사진은 null
    private final String source;       // "GALLERY" or "DAILY_LOG"

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

    public static PhotoGalleryItem ofDailyLogImage(String imageUrl, LocalDateTime recordedAt, String writerName) {
        return new PhotoGalleryItem(null, imageUrl, recordedAt, writerName, null, "DAILY_LOG");
    }
}
