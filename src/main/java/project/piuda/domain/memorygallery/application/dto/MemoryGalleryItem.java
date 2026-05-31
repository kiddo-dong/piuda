package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemoryGalleryItem {

    public enum MediaType { IMAGE, AUDIO }

    private final Long galleryId;           // MemoryGallery 직접 등록 항목만 존재, 일지/음성은 null
    private final MediaType type;
    private final String url;
    private final LocalDateTime recordedAt;
    private final String writerNickname;

    private MemoryGalleryItem(Long galleryId, MediaType type, String url, LocalDateTime recordedAt, String writerNickname) {
        this.galleryId = galleryId;
        this.type = type;
        this.url = url;
        this.recordedAt = recordedAt;
        this.writerNickname = writerNickname;
    }

    public static MemoryGalleryItem ofGalleryImage(Long galleryId, String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new MemoryGalleryItem(galleryId, MediaType.IMAGE, imageUrl, recordedAt, writerNickname);
    }

    public static MemoryGalleryItem ofDailyLogImage(String imageUrl, LocalDateTime recordedAt, String writerNickname) {
        return new MemoryGalleryItem(null, MediaType.IMAGE, imageUrl, recordedAt, writerNickname);
    }

    public static MemoryGalleryItem ofAudio(String audioUrl, LocalDateTime recordedAt) {
        return new MemoryGalleryItem(null, MediaType.AUDIO, audioUrl, recordedAt, null);
    }
}
