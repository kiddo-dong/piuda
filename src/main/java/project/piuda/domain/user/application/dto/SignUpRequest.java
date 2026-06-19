package project.piuda.domain.user.application.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.user.domain.Gender;
import project.piuda.domain.user.domain.Role;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 255, message = "이메일은 255자 이하로 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(max = 100, message = "비밀번호는 100자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
             message = "비밀번호는 8자 이상이며 영문과 숫자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String passwordConfirm;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용 가능합니다.")
    private String nickname;

    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$", message = "올바른 전화번호 형식을 입력해주세요.")
    private String phone;

    @Size(max = 200, message = "자기소개는 200자 이하로 입력해주세요.")
    private String introduction;

    @NotNull(message = "역할을 선택해주세요.")
    private Role role;

    @Min(value = 0, message = "경력 연수는 0 이상이어야 합니다.")
    @Max(value = 50, message = "경력 연수는 50 이하이어야 합니다.")
    private Integer experienceYears;

    private Gender gender;
    private LocalDate birthDate;

    @AssertTrue(message = "비밀번호가 일치하지 않습니다.")
    public boolean isPasswordMatching() {
        return password != null && password.equals(passwordConfirm);
    }
}
