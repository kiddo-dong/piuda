package project.piuda.domain.memorygallery.application.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AudioGalleryItem {

    private final Long audioId;
    private final String audioUrl;
    private final LocalDateTime recordedAt;

    private AudioGalleryItem(Long audioId, String audioUrl, LocalDateTime recordedAt) {
        this.audioId = audioId;
        this.audioUrl = audioUrl;
        this.recordedAt = recordedAt;
    }

    public static AudioGalleryItem of(Long audioId, String audioUrl, LocalDateTime recordedAt) {
        return new AudioGalleryItem(audioId, audioUrl, recordedAt);
    }
}
