package project.piuda.domain.chat.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.piuda.domain.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByUser1AndUser2(User user1, User user2);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.user1 = :user OR cr.user2 = :user " +
           "ORDER BY COALESCE(cr.lastMessageAt, cr.createdAt) DESC")
    List<ChatRoom> findAllByUserOrderByLastMessage(@Param("user") User user);
}
