package project.piuda.domain.chat.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatNotificationResponse {
    private final Long roomId;
    private final String senderNickname;
    private final String lastMessage;
    private final long unreadCount;
}
