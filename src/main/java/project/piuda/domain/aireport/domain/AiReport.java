package project.piuda.domain.aireport.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.patient.domain.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDate weekStart; // 해당 주 월요일

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private CareRecommendation recommendation;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public AiReport(Patient patient, LocalDate weekStart, String content, CareRecommendation recommendation) {
        this.patient = patient;
        this.weekStart = weekStart;
        this.content = content;
        this.recommendation = recommendation;
        this.createdAt = LocalDateTime.now();
    }
}
