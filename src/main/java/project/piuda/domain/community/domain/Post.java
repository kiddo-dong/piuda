package project.piuda.domain.community.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int viewCount;

    @Column(nullable = false)
    private boolean hasAdopted;

    @Column(nullable = false)
    private boolean hidden;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Post(User writer, String title, String content, PostCategory category) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.category = category;
        this.likeCount = 0;
        this.viewCount = 0;
        this.hasAdopted = false;
        this.hidden = false;
        this.createdAt = LocalDateTime.now();
    }

    public void markAdopted() {
        this.hasAdopted = true;
    }

    public void unmarkAdopted() {
        this.hasAdopted = false;
    }

    public void update(String title, String content, PostCategory category) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLike() {
        this.likeCount++;
    }

    public void decreaseLike() {
        if (this.likeCount > 0) this.likeCount--;
    }

    public void hide() {
        this.hidden = true;
    }

    public void unhide() {
        this.hidden = false;
    }
}
