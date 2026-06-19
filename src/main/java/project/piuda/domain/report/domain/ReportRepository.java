package project.piuda.domain.report.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);
    long countByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);
    Page<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status, Pageable pageable);
    void deleteAllByTargetTypeAndTargetId(ReportTargetType targetType, Long targetId);

    void deleteAllByReporter(User reporter);

    @Modifying
    @Query("DELETE FROM Report r WHERE r.targetType = :type AND r.targetId IN :ids")
    void deleteAllByTargetTypeAndTargetIdIn(@Param("type") ReportTargetType type, @Param("ids") List<Long> ids);
}
