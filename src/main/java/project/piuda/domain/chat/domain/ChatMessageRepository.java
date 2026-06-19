package project.piuda.domain.chat.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);

    List<ChatMessage> findByChatRoomAndIdLessThanOrderByCreatedAtDesc(ChatRoom chatRoom, Long cursor, Pageable pageable);

    long countByChatRoomAndSenderNotAndIsReadFalse(ChatRoom chatRoom, User sender);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true " +
           "WHERE m.chatRoom = :room AND m.sender <> :reader AND m.isRead = false")
    void markAllAsRead(@Param("room") ChatRoom room, @Param("reader") User reader);
}
