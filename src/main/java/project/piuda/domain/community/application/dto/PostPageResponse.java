package project.piuda.domain.community.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostPageResponse {
    private final List<PostResponse> posts;
    private final boolean hasNext;
    private final Long nextCursor;

    public PostPageResponse(List<PostResponse> posts, boolean hasNext) {
        this.posts = posts;
        this.hasNext = hasNext;
        this.nextCursor = hasNext ? posts.get(posts.size() - 1).getPostId() : null;
    }
}
