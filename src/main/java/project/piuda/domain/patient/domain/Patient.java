package project.piuda.domain.patient.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // DDD: 무분별한 객체 생성 방지
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private DementiaStage dementiaStage;

    @Column(unique = true, length = 8)
    private String inviteCode;

    private LocalDateTime createdAt;

    @Builder
    public Patient(String name, LocalDate birthDate, Gender gender, DementiaStage dementiaStage) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.dementiaStage = dementiaStage;
        this.createdAt = LocalDateTime.now();
        this.inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    // === 비즈니스 로직 (DDD 사상: Setter 배제, 의미 있는 메서드 사용) ===

    public void update(String name, LocalDate birthDate, Gender gender, DementiaStage dementiaStage) {
        if (name != null && !name.isBlank()) this.name = name;
        if (birthDate != null) this.birthDate = birthDate;
        if (gender != null) this.gender = gender;
        if (dementiaStage != null) this.dementiaStage = dementiaStage;
    }
}