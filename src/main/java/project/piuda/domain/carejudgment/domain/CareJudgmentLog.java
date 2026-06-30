package project.piuda.domain.carejudgment.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

/**
 * 케어 판단 기록 — 간병인이 보호자의 사전 지시 없이 독자적으로 내린 판단을
 * 상황(situation)·판단(action)·근거(rationale) 구조로 남기는 기록.
 * 환자(patient)에 종속되며, 해당 환자에 연결된 사용자(보호자·간병인)가 조회한다.
 */
@Entity
@Table(name = "care_judgment_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareJudgmentLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "judgment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer; // 작성 간병인

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JudgmentCategory category; // 식사 / 이동 / 투약

    @Column(nullable = false, columnDefinition = "TEXT")
    private String situation; // 어떤 상황이었나요

    @Column(nullable = false, columnDefinition = "TEXT")
    private String action;    // 어떻게 했나요 (판단)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rationale; // 왜 그렇게 했나요 (근거)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UrgencyLevel urgency; // 일반 / 관찰필요 / 즉시공유

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public CareJudgmentLog(Patient patient, User writer, JudgmentCategory category,
                           String situation, String action, String rationale, UrgencyLevel urgency) {
        this.patient = patient;
        this.writer = writer;
        this.category = category;
        this.situation = situation;
        this.action = action;
        this.rationale = rationale;
        this.urgency = urgency;
        this.createdAt = LocalDateTime.now();
    }

    public void update(JudgmentCategory category, String situation, String action,
                       String rationale, UrgencyLevel urgency) {
        this.category = category;
        this.situation = situation;
        this.action = action;
        this.rationale = rationale;
        this.urgency = urgency;
        this.updatedAt = LocalDateTime.now();
    }
}
