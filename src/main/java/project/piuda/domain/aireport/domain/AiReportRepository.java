package project.piuda.domain.aireport.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, Long> {
    List<AiReport> findAllByPatientIdOrderByWeekStartDesc(Long patientId);
    Optional<AiReport> findTopByPatientIdOrderByWeekStartDesc(Long patientId);
    boolean existsByPatientIdAndWeekStart(Long patientId, LocalDate weekStart);
    void deleteAllByPatientId(Long patientId);
}
