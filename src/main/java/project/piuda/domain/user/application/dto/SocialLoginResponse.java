package project.piuda.domain.user.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialLoginResponse {
    private String token;
    private boolean needsOnboarding;
}
