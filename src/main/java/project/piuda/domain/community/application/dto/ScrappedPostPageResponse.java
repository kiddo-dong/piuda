package project.piuda.domain.community.application.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ScrappedPostPageResponse {
    private final List<ScrappedPostResponse> posts;
    private final boolean hasNext;
    private final Integer nextPage;

    public ScrappedPostPageResponse(List<ScrappedPostResponse> posts, boolean hasNext, int currentPage) {
        this.posts = posts;
        this.hasNext = hasNext;
        this.nextPage = hasNext ? currentPage + 1 : null;
    }
}
