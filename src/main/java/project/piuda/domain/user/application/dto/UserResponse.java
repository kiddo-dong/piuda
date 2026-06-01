package project.piuda.domain.user.application.dto;

import lombok.Getter;
import project.piuda.domain.user.domain.User;

@Getter
public class UserResponse {
    private final Long userId;
    private final String email;
    private final String name;
    private final String nickname;
    private final String phone;
    private final String profileImageUrl;
    private final String introduction;
    private final String role;
    private final int score;

    public UserResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.nickname = user.getNickname();
        this.phone = user.getPhone();
        this.profileImageUrl = user.getProfileImageUrl();
        this.introduction = user.getIntroduction();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.score = user.getScore();
    }
}
