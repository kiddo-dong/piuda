package project.piuda.domain.careadvice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareAdviceSessionRepository extends JpaRepository<CareAdviceSession, Long> {

    List<CareAdviceSession> findByUserIdAndPatientIdOrderByCreatedAtDesc(Long userId, Long patientId);
}
