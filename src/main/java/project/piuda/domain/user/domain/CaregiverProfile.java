package project.piuda.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "caregiver_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaregiverProfile {

    @Id
    private Long caregiverId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "caregiver_id")
    private User user;

    private int innerScore;
    private int experienceYears;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private CaregiverType caregiverType;

    @Builder
    public CaregiverProfile(User user, int experienceYears, Gender gender, LocalDate birthDate, CaregiverType caregiverType) {
        this.user = user;
        this.experienceYears = experienceYears;
        this.gender = gender;
        this.birthDate = birthDate;
        this.caregiverType = caregiverType;
    }

    public void update(Gender gender, LocalDate birthDate, Integer experienceYears, CaregiverType caregiverType) {
        if (gender != null) this.gender = gender;
        if (birthDate != null) this.birthDate = birthDate;
        if (experienceYears != null) this.experienceYears = experienceYears;
        if (caregiverType != null) this.caregiverType = caregiverType;
    }

    public void addScore(int score) {
        this.innerScore += score;
    }
}