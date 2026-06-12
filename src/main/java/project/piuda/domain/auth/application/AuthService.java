package project.piuda.domain.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.user.application.dto.SocialLoginResponse;
import project.piuda.domain.user.domain.AuthProvider;
import project.piuda.domain.user.domain.RefreshToken;
import project.piuda.domain.user.domain.RefreshTokenRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.infrastructure.GoogleAuthClient;
import project.piuda.global.infrastructure.KakaoAuthClient;
import project.piuda.global.infrastructure.LineAuthClient;
import project.piuda.global.infrastructure.SocialUserInfo;
import project.piuda.global.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final GoogleAuthClient googleAuthClient;
    private final KakaoAuthClient kakaoAuthClient;
    private final LineAuthClient lineAuthClient;

    @Transactional
    public SocialLoginResponse loginWithGoogle(String idToken) {
        SocialUserInfo userInfo = googleAuthClient.verify(idToken);
        return processLogin(userInfo, AuthProvider.GOOGLE);
    }

    @Transactional
    public SocialLoginResponse loginWithKakao(String accessToken) {
        SocialUserInfo userInfo = kakaoAuthClient.getUserInfo(accessToken);
        return processLogin(userInfo, AuthProvider.KAKAO);
    }

    @Transactional
    public SocialLoginResponse loginWithLine(String idToken) {
        SocialUserInfo userInfo = lineAuthClient.verify(idToken);
        return processLogin(userInfo, AuthProvider.LINE);
    }

    private SocialLoginResponse processLogin(SocialUserInfo userInfo, AuthProvider provider) {
        User user = userRepository.findByProviderAndProviderId(provider, userInfo.id())
                .orElseGet(() -> createUser(userInfo, provider));

        String roleStr = user.getRole() != null ? user.getRole().name() : "NONE";
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), roleStr);
        String refreshTokenValue = jwtTokenProvider.createRefreshToken();
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                rt -> rt.rotate(refreshTokenValue, jwtTokenProvider.getRefreshTokenExpiry()),
                () -> refreshTokenRepository.save(RefreshToken.builder()
                        .user(user)
                        .token(refreshTokenValue)
                        .expiryDate(jwtTokenProvider.getRefreshTokenExpiry())
                        .build())
        );

        return new SocialLoginResponse(accessToken, refreshTokenValue, !user.isOnboardingDone());
    }

    private User createUser(SocialUserInfo userInfo, AuthProvider provider) {
        String email = userInfo.email();
        if (email == null || email.isBlank()) {
            email = provider.name().toLowerCase() + "_" + userInfo.id() + "@oauth.piuda";
        }
        User user = User.ofOAuth2(email, userInfo.name(), userInfo.imageUrl(), provider, userInfo.id());
        return userRepository.save(user);
    }
}
