package project.piuda.domain.admin.application.dto;

import lombok.Getter;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Getter
public class AdminUserResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final String nickname;
    private final String role;
    private final String provider;
    private final int score;
    private final LocalDateTime createdAt;

    public AdminUserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.provider = user.getProvider() != null ? user.getProvider().name() : null;
        this.score = user.getScore();
        this.createdAt = user.getCreatedAt();
    }
}
