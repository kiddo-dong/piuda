package project.piuda.domain.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import project.piuda.domain.user.domain.Gender;
import project.piuda.domain.user.domain.Role;

import java.time.LocalDate;

@Getter
public class OnboardingRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    private String phone;

    private Integer experienceYears;
    private Gender gender;
    private LocalDate birthDate;
}
