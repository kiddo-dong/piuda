package project.piuda.domain.careadvice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface CareAdviceSessionRepository extends JpaRepository<CareAdviceSession, Long> {

    List<CareAdviceSession> findByUserIdAndPatientIdOrderByCreatedAtDesc(Long userId, Long patientId);

    List<CareAdviceSession> findByCreatedAtBefore(LocalDateTime cutoff);

    List<CareAdviceSession> findAllByUser(User user);
}
