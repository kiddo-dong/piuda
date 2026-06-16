package project.piuda.global.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import project.piuda.global.exception.BusinessException;

import java.util.Map;

@Slf4j
@Component
public class KakaoAuthClient {

    private final RestClient restClient = RestClient.create();

    @SuppressWarnings("unchecked")
    public SocialUserInfo getUserInfo(String accessToken) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://kapi.kakao.com/v2/user/me")
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response == null) throw new BusinessException("Kakao 사용자 정보 조회에 실패했습니다.");

            String id = String.valueOf(response.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
            String email = null;
            String name = null;
            String imageUrl = null;

            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                if (profile != null) {
                    name = (String) profile.get("nickname");
                    imageUrl = (String) profile.get("profile_image_url");
                }
            }

            return new SocialUserInfo(id, name, email, imageUrl);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Kakao 토큰 검증 실패", e);
            throw new BusinessException("유효하지 않은 Kakao Access Token입니다.");
        }
    }
}
