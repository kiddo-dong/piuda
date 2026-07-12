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

    // 여러 채팅방의 안읽음 수를 한 번에 집계 (getMyRooms N+1 방지) — [roomId, count]
    @Query("SELECT m.chatRoom.id, COUNT(m) FROM ChatMessage m " +
           "WHERE m.chatRoom IN :rooms AND m.sender <> :me AND m.isRead = false " +
           "GROUP BY m.chatRoom.id")
    List<Object[]> countUnreadGroupedByRoom(@Param("rooms") List<ChatRoom> rooms, @Param("me") User me);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true " +
           "WHERE m.chatRoom = :room AND m.sender <> :reader AND m.isRead = false")
    void markAllAsRead(@Param("room") ChatRoom room, @Param("reader") User reader);

    void deleteAllByChatRoomIn(List<ChatRoom> rooms);
}
