package project.piuda.domain.careadvice.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareAdviceMessageRepository extends JpaRepository<CareAdviceMessage, Long> {

    List<CareAdviceMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId);

    @Query("SELECT m FROM CareAdviceMessage m WHERE m.session.id = :sessionId ORDER BY m.createdAt DESC")
    List<CareAdviceMessage> findRecentMessages(@Param("sessionId") Long sessionId, Pageable pageable);

    void deleteAllBySessionIn(List<CareAdviceSession> sessions);
}
