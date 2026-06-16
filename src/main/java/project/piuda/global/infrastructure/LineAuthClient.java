package project.piuda.global.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import project.piuda.global.exception.BusinessException;

import java.util.Map;

@Slf4j
@Component
public class LineAuthClient {

    private final RestClient restClient = RestClient.create();

    @Value("${line.client-id}")
    private String clientId;

    @SuppressWarnings("unchecked")
    public SocialUserInfo verify(String idToken) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("id_token", idToken);
            form.add("client_id", clientId);

            Map<String, Object> response = restClient.post()
                    .uri("https://api.line.me/oauth2/v2.1/verify")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(Map.class);

            if (response == null) throw new BusinessException("Line 토큰 검증에 실패했습니다.");

            return new SocialUserInfo(
                    (String) response.get("sub"),
                    (String) response.get("name"),
                    (String) response.get("email"),
                    (String) response.get("picture")
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Line 토큰 검증 실패", e);
            throw new BusinessException("유효하지 않은 Line ID Token입니다.");
        }
    }
}
