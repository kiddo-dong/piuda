package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostCategory;
import project.piuda.domain.community.domain.PostImage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostResponse {
    private final Long postId;
    private final String writerNickname;
    private final String writerProfileImageUrl;
    private final String writerRole;
    private final String title;
    private final String content;
    private final PostCategory category;
    private final List<String> imageUrls;
    private final int likeCount;
    private final int viewCount;
    private final boolean likedByMe;
    private final boolean scrappedByMe;
    private final boolean hasAdopted;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Post post, boolean likedByMe, boolean scrappedByMe) {
        this.postId = post.getId();
        this.writerNickname = post.getWriter().getNickname();
        this.writerProfileImageUrl = post.getWriter().getProfileImageUrl();
        this.writerRole = post.getWriter().getRole() != null ? post.getWriter().getRole().name() : null;
        this.title = post.getTitle();
        this.content = post.getContent();
        this.category = post.getCategory();
        this.imageUrls = post.getImages().stream()
                .map(PostImage::getImageUrl)
                .collect(Collectors.toList());
        this.likeCount = post.getLikeCount();
        this.viewCount = post.getViewCount();
        this.likedByMe = likedByMe;
        this.scrappedByMe = scrappedByMe;
        this.hasAdopted = post.isHasAdopted();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}
