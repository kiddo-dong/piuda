package project.piuda.domain.user.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FcmTokenRequest {

    @NotBlank(message = "FCM 토큰을 입력해주세요.")
    private String fcmToken;
}
