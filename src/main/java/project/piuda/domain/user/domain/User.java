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

    @Column(nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private int score;

    private LocalDateTime createdAt;

    @Builder
    public User(String email, String password, String name, String phone, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
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

    public void update(String name, String phone, String encodedPassword) {
        if (name != null && !name.isBlank()) this.name = name;
        if (phone != null && !phone.isBlank()) this.phone = phone;
        if (encodedPassword != null) this.password = encodedPassword;
    }
}