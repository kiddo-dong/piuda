package project.piuda.domain.admin.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Post;

import java.time.LocalDateTime;

@Getter
public class AdminPostResponse {
    private final Long id;
    private final String title;
    private final String category;
    private final String writerEmail;
    private final String writerNickname;
    private final int viewCount;
    private final int likeCount;
    private final LocalDateTime createdAt;

    public AdminPostResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.category = post.getCategory().name();
        this.writerEmail = post.getWriter().getEmail();
        this.writerNickname = post.getWriter().getNickname();
        this.viewCount = post.getViewCount();
        this.likeCount = post.getLikeCount();
        this.createdAt = post.getCreatedAt();
    }
}
