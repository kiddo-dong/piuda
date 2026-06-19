package project.piuda.domain.chat.application.dto;

import lombok.Getter;
import project.piuda.domain.chat.domain.ChatRoom;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Getter
public class ChatRoomResponse {
    private final Long roomId;
    private final String otherNickname;
    private final String otherProfileImageUrl;
    private final String lastMessage;
    private final LocalDateTime lastMessageAt;
    private final long unreadCount;

    public ChatRoomResponse(ChatRoom room, User me, long unreadCount) {
        User other = room.getOtherUser(me);
        this.roomId = room.getId();
        this.otherNickname = other.getNickname();
        this.otherProfileImageUrl = other.getProfileImageUrl();
        this.lastMessage = room.getLastMessage();
        this.lastMessageAt = room.getLastMessageAt();
        this.unreadCount = unreadCount;
    }
}
