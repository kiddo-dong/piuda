package project.piuda.domain.user.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.user.domain.Role;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private Role role;

    // 간병인 전용 선택 필드
    private Integer experienceYears;
    private String introduction;
}