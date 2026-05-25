package project.piuda.domain.calendar.domain;

import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.dailylog.domain.DailyLog;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "care_calendars")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "calendar_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee; // 수동 일정의 역할을 분담할 가족/간병인 담당자

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private DailyLog dailyLog; // 하루 일지 연동용 (이 값이 존재하면 해당 일에 일지 작성 완료로 판단)

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CalendarType calendarType; // DAILY_LOG(일지연동체크) 또는 SCHEDULE(수동스케줄)

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CalendarCategory category; // OUTING, VISIT, SUPPLY, EVENT, ETC

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Builder
    public CareCalendar(Patient patient, User writer, User assignee, DailyLog dailyLog,
                        String title, String content, CalendarType calendarType,
                        CalendarCategory category, LocalDateTime startTime, LocalDateTime endTime) {
        this.patient = patient;
        this.writer = writer;
        this.assignee = assignee;
        this.dailyLog = dailyLog;
        this.title = title;
        this.content = content;
        this.calendarType = calendarType;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // 일정 수정 로직
    public void updateSchedule(String title, String content, User assignee,
                               CalendarCategory category, LocalDateTime startTime, LocalDateTime endTime) {
        if (this.calendarType == CalendarType.DAILY_LOG) {
            throw new IllegalStateException("하루 일지 자동 연동 마크는 임의로 수정할 수 없습니다.");
        }
        this.title = title;
        this.content = content;
        this.assignee = assignee;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}