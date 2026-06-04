package project.piuda.domain.community.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class PostPageResponse {
    private final List<PostResponse> posts;
    private final boolean hasNext;
    private final Long nextCursor;  // LATEST 정렬 시 사용
    private final Integer nextPage; // VIEWS/LIKES 정렬 시 사용

    // LATEST (커서 페이징)
    public PostPageResponse(List<PostResponse> posts, boolean hasNext) {
        this.posts = posts;
        this.hasNext = hasNext;
        this.nextCursor = hasNext ? posts.get(posts.size() - 1).getPostId() : null;
        this.nextPage = null;
    }

    // VIEWS / LIKES (오프셋 페이징)
    public PostPageResponse(List<PostResponse> posts, boolean hasNext, int currentPage) {
        this.posts = posts;
        this.hasNext = hasNext;
        this.nextCursor = null;
        this.nextPage = hasNext ? currentPage + 1 : null;
    }
}
