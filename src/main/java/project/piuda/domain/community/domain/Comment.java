package project.piuda.domain.community.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isAdopted;

    @Column(nullable = false)
    private boolean hidden;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Comment(Post post, User writer, Comment parentComment, String content) {
        this.post = post;
        this.writer = writer;
        this.parentComment = parentComment;
        this.content = content;
        this.isAdopted = false;
        this.hidden = false;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String content) {
        this.content = content;
    }

    public void adopt() {
        this.isAdopted = true;
    }

    public void cancelAdoption() {
        this.isAdopted = false;
    }

    public void hide() {
        this.hidden = true;
    }

    public void unhide() {
        this.hidden = false;
    }
}
