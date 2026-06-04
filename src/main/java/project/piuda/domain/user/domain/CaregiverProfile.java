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

    @Builder
    public CaregiverProfile(User user, int experienceYears, Gender gender, LocalDate birthDate) {
        this.user = user;
        this.experienceYears = experienceYears;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void addScore(int score) {
        this.innerScore += score;
    }
}