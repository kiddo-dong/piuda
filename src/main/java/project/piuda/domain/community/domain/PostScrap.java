package project.piuda.domain.community.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_scraps", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime scrappedAt;

    @Builder
    public PostScrap(Post post, User user) {
        this.post = post;
        this.user = user;
        this.scrappedAt = LocalDateTime.now();
    }
}
