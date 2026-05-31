package project.piuda.domain.user.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String nickname;
    private String phone;
    private String profileImageUrl;
    private String introduction;
    private String password;
}
