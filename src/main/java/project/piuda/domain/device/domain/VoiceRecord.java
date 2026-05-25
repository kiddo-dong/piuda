package project.piuda.domain.device.domain;

import project.piuda.domain.patient.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voice_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VoiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voice_record_id")
    private Long id;

    // 데이터 귀속 주체는 환자 (디바이스는 녹음 수단)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false, length = 512)
    private String audioUrl;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Builder
    public VoiceRecord(Patient patient, String audioUrl, LocalDateTime recordedAt) {
        this.patient = patient;
        this.audioUrl = audioUrl;
        this.recordedAt = recordedAt;
    }
}
