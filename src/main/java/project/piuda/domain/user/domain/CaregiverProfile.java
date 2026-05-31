package project.piuda.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

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

    @Builder
    public CaregiverProfile(User user, int experienceYears) {
        this.user = user;
        this.experienceYears = experienceYears;
    }

    public void addScore(int score) {
        this.innerScore += score;
    }
}