package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostCategory;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private final Long postId;
    private final String writerName;
    private final String writerRole;
    private final String title;
    private final String content;
    private final PostCategory category;
    private final String imageUrl;
    private final int likeCount;
    private final boolean likedByMe;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Post post, boolean likedByMe) {
        this.postId = post.getId();
        this.writerName = post.getWriter().getName();
        this.writerRole = post.getWriter().getRole().name();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.likedByMe = likedByMe;
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
