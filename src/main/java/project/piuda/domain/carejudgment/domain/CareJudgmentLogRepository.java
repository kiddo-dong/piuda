package project.piuda.domain.carejudgment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface CareJudgmentLogRepository extends JpaRepository<CareJudgmentLog, Long> {

    List<CareJudgmentLog> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    List<CareJudgmentLog> findByPatientIdAndCategoryOrderByCreatedAtDesc(Long patientId, JudgmentCategory category);

    long countByPatientIdAndUrgency(Long patientId, UrgencyLevel urgency);

    void deleteAllByPatientId(Long patientId);

    void deleteAllByWriter(User writer);
}
