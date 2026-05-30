package project.piuda.domain.user.application.dto;

import lombok.Getter;
import project.piuda.domain.user.domain.User;

@Getter
public class UserResponse {
    private final Long userId;
    private final String email;
    private final String name;
    private final String phone;
    private final String role;
    private final int score;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.role = user.getRole().name();
        this.score = user.getScore();
    }
}
