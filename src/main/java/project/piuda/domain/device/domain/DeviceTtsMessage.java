package project.piuda.domain.device.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_tts_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceTtsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    @Column(nullable = false, length = 512)
    private String audioUrl;

    @Column(nullable = false)
    private boolean played;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public DeviceTtsMessage(Device device, String text, String audioUrl) {
        this.device    = device;
        this.text      = text;
        this.audioUrl  = audioUrl;
        this.played    = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markPlayed() {
        this.played = true;
    }
}
