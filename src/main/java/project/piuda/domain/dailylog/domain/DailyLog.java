package project.piuda.domain.dailylog.domain;

import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false)
    private LocalDate logDate;

    @Enumerated(EnumType.STRING)
    private DailyStatus mealStatus; // 타입 변경

    private boolean medicationStatus;

    @Enumerated(EnumType.STRING)
    private DailyStatus sleepStatus; // 타입 Medicine

    private boolean walkStatus;

    @Column(columnDefinition = "TEXT")
    private String abnormalBehavior;

    @Column(columnDefinition = "TEXT")
    private String generalNote;

    @Builder
    public DailyLog(Patient patient, User writer, LocalDate logDate, DailyStatus mealStatus,
                    boolean medicationStatus, DailyStatus sleepStatus, boolean walkStatus,
                    String abnormalBehavior, String generalNote) {
        this.patient = patient;
        this.writer = writer;
        this.logDate = logDate;
        this.mealStatus = mealStatus;
        this.medicationStatus = medicationStatus;
        this.sleepStatus = sleepStatus;
        this.walkStatus = walkStatus;
        this.abnormalBehavior = abnormalBehavior;
        this.generalNote = generalNote;
    }

    // 비즈니스 수정 로직
    public void updateLog(DailyStatus mealStatus, boolean medicationStatus, DailyStatus sleepStatus,
                          boolean walkStatus, String abnormalBehavior, String generalNote) {
        this.mealStatus = mealStatus;
        this.medicationStatus = medicationStatus;
        this.sleepStatus = sleepStatus;
        this.walkStatus = walkStatus;
        this.abnormalBehavior = abnormalBehavior;
        this.generalNote = generalNote;
    }
}