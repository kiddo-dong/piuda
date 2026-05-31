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

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(length = 20)
    private String phone;

    @Column(length = 512)
    private String profileImageUrl;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private int score;

    private LocalDateTime createdAt;

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
        this.score = 100;
        this.createdAt = LocalDateTime.now();
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public void subtractScore(int amount) {
        this.score = Math.max(0, this.score - amount);
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