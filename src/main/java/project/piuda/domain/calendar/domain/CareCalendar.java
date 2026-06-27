package project.piuda.domain.calendar.domain;

import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;
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

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CalendarType calendarType; // SCHEDULE(수동스케줄)

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CalendarCategory category; // OUTING, VISIT, SUPPLY, EVENT, ETC

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Builder
    public CareCalendar(Patient patient, User writer, User assignee,
                        String title, String content, CalendarType calendarType,
                        CalendarCategory category, LocalDateTime startTime, LocalDateTime endTime) {
        this.patient = patient;
        this.writer = writer;
        this.assignee = assignee;
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
        this.title = title;
        this.content = content;
        this.assignee = assignee;
        this.category = category;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}