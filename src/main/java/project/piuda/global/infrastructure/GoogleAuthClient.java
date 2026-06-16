package project.piuda.global.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import project.piuda.global.exception.BusinessException;

import java.util.Map;

@Slf4j
@Component
public class GoogleAuthClient {

    private final RestClient restClient = RestClient.create();

    @Value("${google.client-id}")
    private String googleClientId;

    @SuppressWarnings("unchecked")
    public SocialUserInfo verify(String idToken) {
        try {
            Map<String, Object> response = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken)
                    .retrieve()
                    .body(Map.class);

            if (response == null) throw new BusinessException("Google 토큰 검증에 실패했습니다.");

            String aud = (String) response.get("aud");
            if (!googleClientId.equals(aud)) {
                throw new BusinessException("유효하지 않은 Google ID Token입니다.");
            }

            return new SocialUserInfo(
                    (String) response.get("sub"),
                    (String) response.get("name"),
                    (String) response.get("email"),
                    (String) response.get("picture")
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google 토큰 검증 실패", e);
            throw new BusinessException("유효하지 않은 Google ID Token입니다.");
        }
    }
}
