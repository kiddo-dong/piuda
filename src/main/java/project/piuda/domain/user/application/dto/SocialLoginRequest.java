package project.piuda.domain.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SocialLoginRequest {

    @NotBlank(message = "토큰은 필수입니다.")
    private String token;
}
