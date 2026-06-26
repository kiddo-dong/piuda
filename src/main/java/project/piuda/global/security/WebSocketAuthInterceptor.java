package project.piuda.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import project.piuda.domain.chat.domain.ChatRoom;
import project.piuda.domain.chat.domain.ChatRoomRepository;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private static final String CHAT_TOPIC_PREFIX = "/topic/chat/";

    private final JwtTokenProvider jwtTokenProvider;
    private final ChatRoomRepository chatRoomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateConnect(accessor);
        } else if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscribe(accessor);
        }
        return message;
    }

    private void authenticateConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new MessagingException("WebSocket 연결 실패: Authorization 헤더가 없습니다.");
        }
        String token = authHeader.substring(7);
        if (!jwtTokenProvider.validateToken(token)) {
            throw new MessagingException("WebSocket 연결 실패: 유효하지 않은 토큰입니다.");
        }
        String email = jwtTokenProvider.getEmail(token);
        accessor.setUser(new UsernamePasswordAuthenticationToken(email, null, List.of()));
    }

    /**
     * /topic/chat/{roomId} 구독 시 해당 채팅방의 참여자인지 검증한다.
     * (CONNECT만 검증하면 인증된 사용자가 임의의 방을 구독해 도청할 수 있음)
     */
    private void authorizeSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(CHAT_TOPIC_PREFIX)) {
            return; // 채팅방 토픽이 아니면 검증 대상 아님
        }

        Long roomId = parseRoomId(destination);
        if (roomId == null) {
            return; // roomId 형태가 아니면 통과 (다른 토픽 영향 없음)
        }

        Principal principal = accessor.getUser();
        if (principal == null) {
            throw new MessagingException("WebSocket 구독 실패: 인증 정보가 없습니다.");
        }
        String email = principal.getName();

        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new MessagingException("WebSocket 구독 실패: 존재하지 않는 채팅방입니다."));

        boolean member = email.equals(room.getUser1().getEmail()) || email.equals(room.getUser2().getEmail());
        if (!member) {
            throw new MessagingException("WebSocket 구독 실패: 채팅방 접근 권한이 없습니다.");
        }
    }

    private Long parseRoomId(String destination) {
        String tail = destination.substring(CHAT_TOPIC_PREFIX.length());
        int slash = tail.indexOf('/');
        String idPart = (slash >= 0) ? tail.substring(0, slash) : tail; // /topic/chat/{id}/read 형태 대응
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
