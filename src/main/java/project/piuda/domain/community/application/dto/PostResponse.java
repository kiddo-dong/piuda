package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostCategory;

import java.time.LocalDateTime;

@Getter
public class PostResponse {
    private final Long postId;
    private final String writerNickname;
    private final String writerRole;
    private final String title;
    private final String content;
    private final PostCategory category;
    private final String imageUrl;
    private final int likeCount;
    private final int viewCount;
    private final boolean likedByMe;
    private final boolean hasAdopted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Post post, boolean likedByMe) {
        this.postId = post.getId();
        this.writerNickname = post.getWriter().getNickname();
        this.writerRole = post.getWriter().getRole().name();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.imageUrl = post.getImageUrl();
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.likedByMe = likedByMe;
        this.hasAdopted = post.isHasAdopted();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
