package project.piuda.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.io.Serializable;

@Entity
@Table(name = "users", indexes = @Index(name = "idx_user_score", columnList = "score DESC"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 512)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column
    private Role role;

    @Column(nullable = false)
    private int score;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AuthProvider provider;

    @Column(length = 100)
    private String providerId;

    @Column(nullable = false)
    private boolean onboardingDone;

    @Builder
    public User(String email, String password, String name, String nickname, String phone,
                String profileImageUrl, String introduction, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.introduction = introduction;
        this.role = role;
        this.provider = AuthProvider.LOCAL;
        this.onboardingDone = true;
        this.score = 100;
        this.createdAt = LocalDateTime.now();
    }

    public static User ofOAuth2(String email, String name, String profileImageUrl,
                                AuthProvider provider, String providerId) {
        User user = new User();
        user.email = email;
        user.name = name;
        user.profileImageUrl = profileImageUrl;
        user.provider = provider;
        user.providerId = providerId;
        user.onboardingDone = false;
        user.score = 100;
        user.createdAt = LocalDateTime.now();
        return user;
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public void subtractScore(int amount) {
        this.score = Math.max(0, this.score - amount);
    }

    public void completeOnboarding(String nickname, Role role, String phone) {
        this.nickname = nickname;
        this.role = role;
        if (phone != null && !phone.isBlank()) this.phone = phone;
        this.onboardingDone = true;
    }

    public void update(String name, String nickname, String phone, String profileImageUrl,
                       String introduction, String encodedPassword) {
        if (name != null && !name.isBlank()) this.name = name;
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (phone != null && !phone.isBlank()) this.phone = phone;
        if (profileImageUrl != null && !profileImageUrl.isBlank()) this.profileImageUrl = profileImageUrl;
        if (introduction != null) this.introduction = introduction;
        if (encodedPassword != null) this.password = encodedPassword;
    }
}