package project.piuda.domain.user.application.dto;

import lombok.Getter;
import project.piuda.domain.user.domain.User;

@Getter
public class RankingResponse {
    private final int rank;
    private final Long userId;
    private final String name;
    private final String role;
    private final int score;
    private final String profileImageUrl;

    public RankingResponse(int rank, User user) {
        this.rank = rank;
        this.userId = user.getId();
        this.name = user.getName();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.score = user.getScore();
        this.profileImageUrl = user.getProfileImageUrl();
    }
}
