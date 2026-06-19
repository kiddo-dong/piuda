package project.piuda.domain.chat.application.dto;

import lombok.Getter;
import project.piuda.domain.chat.domain.ChatMessage;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Getter
public class ChatMessageResponse {
    private final Long messageId;
    private final String senderNickname;
    private final String content;
    private final boolean read;
    private final boolean mine;
    private final LocalDateTime createdAt;

    public ChatMessageResponse(ChatMessage message, User me) {
        this.messageId = message.getId();
        this.senderNickname = message.getSender().getNickname();
        this.content = message.getContent();
        this.read = message.isRead();
        this.mine = message.getSender().getId().equals(me.getId());
        this.createdAt = message.getCreatedAt();
    }
}
