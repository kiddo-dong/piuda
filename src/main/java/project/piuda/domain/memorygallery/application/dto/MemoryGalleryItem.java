package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemoryGalleryItem {

    public enum MediaType { IMAGE, AUDIO }

    private final MediaType type;
    private final String url;
    private final LocalDateTime recordedAt; // 정렬 및 표시용 기준 시각
    private final String writerName;        // 이미지의 경우 일지 작성자, 음성은 null

    private MemoryGalleryItem(MediaType type, String url, LocalDateTime recordedAt, String writerName) {
        this.type = type;
        this.url = url;
        this.recordedAt = recordedAt;
        this.writerName = writerName;
    }

    public static MemoryGalleryItem ofImage(String imageUrl, LocalDateTime recordedAt, String writerName) {
        return new MemoryGalleryItem(MediaType.IMAGE, imageUrl, recordedAt, writerName);
    }

    public static MemoryGalleryItem ofAudio(String audioUrl, LocalDateTime recordedAt) {
        return new MemoryGalleryItem(MediaType.AUDIO, audioUrl, recordedAt, null);
    }
}
