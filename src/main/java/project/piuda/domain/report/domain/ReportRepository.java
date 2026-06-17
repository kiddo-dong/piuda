package project.piuda.domain.report.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);
    long countByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);
    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    void deleteAllByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);
}
