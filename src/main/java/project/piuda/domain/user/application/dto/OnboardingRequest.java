package project.piuda.domain.user.application.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import project.piuda.domain.user.domain.Gender;
import project.piuda.domain.user.domain.Role;

import java.time.LocalDate;

@Getter
public class OnboardingRequest {

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    private String nickname;

    @NotNull(message = "역할은 필수입니다.")
    private Role role;

    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "올바른 전화번호 형식을 입력해주세요.")
    private String phone;

    @Min(value = 0, message = "경력 연수는 0 이상이어야 합니다.")
    @Max(value = 50, message = "경력 연수는 50 이하이어야 합니다.")
    private Integer experienceYears;

    private Gender gender;
    private LocalDate birthDate;
}
