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

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Builder
    public CaregiverProfile(User user, int experienceYears, String introduction) {
        this.user = user;
        this.caregiverId = user.getId();
        this.innerScore = 0; // 초기 점수 0
        this.experienceYears = experienceYears;
        this.introduction = introduction;
    }

    public void addScore(int score) {
        this.innerScore += score;
    }
}