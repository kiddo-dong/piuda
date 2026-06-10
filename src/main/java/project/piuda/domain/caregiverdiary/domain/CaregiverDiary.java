package project.piuda.domain.caregiverdiary.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "caregiver_diaries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaregiverDiary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoodType mood;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CaregiverDiary(User user, String title, String content, MoodType mood) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String title, String content, MoodType mood) {
        this.title = title;
        this.content = content;
        this.mood = mood;
        this.updatedAt = LocalDateTime.now();
    }
}
