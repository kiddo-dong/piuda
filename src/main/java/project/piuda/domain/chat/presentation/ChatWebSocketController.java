package project.piuda.domain.chat.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import project.piuda.domain.chat.application.ChatService;
import project.piuda.domain.chat.application.dto.ChatMessageResponse;
import project.piuda.domain.chat.application.dto.SendMessageRequest;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 클라이언트가 SEND /app/chat/{roomId} 로 메시지 전송 시 호출
     * 저장 후 /topic/chat/{roomId} 구독자 전체에게 브로드캐스트
     */
    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable Long roomId,
                            @Payload SendMessageRequest request,
                            Principal principal) {
        ChatMessageResponse response = chatService.sendMessage(roomId, principal.getName(), request.getContent(), request.getMessageType());
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, response);
    }
}
