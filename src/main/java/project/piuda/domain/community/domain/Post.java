package project.piuda.domain.community.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;


    // -> 추 후 카테고리 추가

    private String imageUrl;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Post(User writer, String title, String content, String imageUrl) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.likeCount = 0;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) this.likeCount--;
    }
}
