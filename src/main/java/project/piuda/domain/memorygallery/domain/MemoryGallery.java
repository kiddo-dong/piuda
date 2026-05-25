package project.piuda.domain.memorygallery.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "memory_galleries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoryGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gallery_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private String imageUrl;

    private String memo;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    @Builder
    public MemoryGallery(Patient patient, User writer, String imageUrl, String memo) {
        this.patient = patient;
        this.writer = writer;
        this.imageUrl = imageUrl;
        this.memo = memo;
        this.uploadedAt = LocalDateTime.now();
    }
}
