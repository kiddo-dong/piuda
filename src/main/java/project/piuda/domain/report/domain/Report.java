package project.piuda.domain.report.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports",
        uniqueConstraints = @UniqueConstraint(columnNames = {"reporter_id", "target_type", "target_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportTargetType targetType;

    @Column(nullable = false)
    private Long targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public Report(User reporter, ReportTargetType targetType, Long targetId, ReportReason reason) {
        this.reporter = reporter;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void dismiss() {
        this.status = ReportStatus.DISMISSED;
    }
}
