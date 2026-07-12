package project.piuda.global.infrastructure;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;

@Slf4j
@Service
public class FcmService {

    private final FirebaseApp firebaseApp;

    public FcmService(@Value("${fcm.service-account-key-path:}") String keyPath) {
        FirebaseApp app = null;
        if (!keyPath.isBlank()) {
            try {
                FileInputStream is = new FileInputStream(keyPath);
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(is))
                        .build();
                app = FirebaseApp.getApps().isEmpty()
                        ? FirebaseApp.initializeApp(options)
                        : FirebaseApp.getInstance();
                // 실행 시 log
                log.info("FCM 초기화 완료");
            } catch (Exception e) {
                log.warn("FCM 초기화 실패 — 푸쉬 알림이 비활성화됩니다.", e);
            }
        } else {
            log.info("fcm.service-account-key-path 미설정 — 푸쉬 알림 비활성화");
        }
        this.firebaseApp = app;
    }

    @Async
    public void send(String fcmToken, String title, String body) {
        send(fcmToken, title, body, null);
    }

    // 외부 HTTP 호출이므로 요청 스레드/트랜잭션을 점유하지 않도록 비동기 처리
    @Async
    public void send(String fcmToken, String title, String body, Long roomId) {
        if (firebaseApp == null || fcmToken == null) return;
        try {
            Message.Builder builder = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());
            if (roomId != null) {
                builder.putData("roomId", String.valueOf(roomId));
            }
            FirebaseMessaging.getInstance(firebaseApp).send(builder.build());
        } catch (Exception e) {
            log.warn("FCM 푸쉬 알림 전송 실패: token={}", fcmToken, e);
        }
    }
}
