package project.piuda.domain.chat.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String fileName;

    @Column(nullable = false)
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public ChatMessage(ChatRoom chatRoom, User sender, MessageType messageType, String content, String fileName) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.messageType = messageType != null ? messageType : MessageType.TEXT;
        this.content = content;
        this.fileName = fileName;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
}
