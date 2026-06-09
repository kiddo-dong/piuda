package project.piuda.domain.user.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.user.domain.Gender;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    private String name;
    private String nickname;
    private String phone;
    private String introduction;
    private String currentPassword;
    private String password;
    private Gender gender;
    private LocalDate birthDate;
    private Integer experienceYears;
}
