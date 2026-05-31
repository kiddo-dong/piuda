package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemoryGalleryItem {

    private final Long galleryId;       // 직접 등록 사진만 존재, 일지 사진은 null
    private final Long audioId;         // 음성 기록만 존재
    private final String url;
    private final LocalDateTime recordedAt;
    private final String writerNickname;

    private MemoryGalleryItem(Long galleryId, Long audioId, String url, LocalDateTime recordedAt, String writerNickname) {
        this.galleryId = galleryId;
        this.audioId = audioId;
        this.url = url;
        this.recordedAt = recordedAt;
        this.writerNickname = writerNickname;
    }

    public static MemoryGalleryItem ofGalleryImage(Long galleryId, String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new MemoryGalleryItem(galleryId, null, imageUrl, recordedAt, writerNickname);
    }

    public static MemoryGalleryItem ofDailyLogImage(String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new MemoryGalleryItem(null, null, imageUrl, recordedAt, writerNickname);
    }

    public static MemoryGalleryItem ofAudio(Long audioId, String audioUrl, LocalDateTime recordedAt) {
        return new MemoryGalleryItem(null, audioId, audioUrl, recordedAt, null);
    }
}
