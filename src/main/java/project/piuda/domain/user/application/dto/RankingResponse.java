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

    public RankingResponse(int rank, User user) {
        this.rank = rank;
        this.userId = user.getId();
        this.name = user.getName();
        this.role = user.getRole().name();
        this.score = user.getScore();
    }
}
