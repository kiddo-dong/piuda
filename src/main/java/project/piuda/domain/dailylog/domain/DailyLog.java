package project.piuda.domain.dailylog.domain;

import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

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

    // 1 & 2. 날짜 및 시간 (서버 시간 기준)
    @Column(nullable = false)
    private LocalDate logDate;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // 3. 신체활동 지원 (CheckBox는 boolean으로 매핑)
    private boolean physicalHygiene;       // 개인위생
    private boolean physicalBath;          // 몸씻기
    private boolean physicalMealHelp;      // 식사도움
    private boolean physicalPositionChange;// 체위변경
    private boolean physicalMobilityHelp;  // 이동도움
    private boolean physicalToiletHelp;    // 화장실 이용
    private int physicalTotalMinutes;      // 신체활동 총 제공시간 (분)

    // 4. 인지활동 지원 (분)
    private int cognitiveStimulationMinutes; // 인지 자극 활동
    private int cognitiveLifeTogetherMinutes; // 일상생활 함께하기

    // 5. 인지 관리 지원 (분)
    private int cognitiveBehaviorManagementMinutes; // 인지 행동변화 관리

    // 6. 정서 지원 (분)
    private int emotionalCommunicationMinutes; // 의사 소통 도움 (간병인 전용)

    // 7. 가사 및 일상 생활 지원
    private boolean householdMealClean;    // 식사 준비 및 청소
    private boolean householdPersonalHelp; // 개인 활동 지원
    private int householdTotalMinutes;     // 가사 및 일상 생활 총 제공시간 (분)

    // 8. 변화상태
    @Enumerated(EnumType.STRING)
    private HealthTrend physicalFunctionTrend; // 신체기능 (호전, 유지, 약화)

    @Enumerated(EnumType.STRING)
    private HealthTrend mealFunctionTrend;     // 식사기능 (호전, 유지, 약화)

    private int bowelIncontinenceCount;        // 대변 실수 횟수
    private int urineIncontinenceCount;        // 소변 실수 횟수

    // 9. 특이사항
    @Column(columnDefinition = "TEXT")
    private String specialNotes;

    // 10. 앱에서 촬영하거나 업로드한 사진의 저장 경로 URL -> 갤러리 기능
    @Column(length = 512)
    private String imageUrl;

    @Builder
    public DailyLog(Patient patient, User writer, LocalDate logDate, LocalTime startTime, LocalTime endTime,
                    boolean physicalHygiene, boolean physicalBath, boolean physicalMealHelp, boolean physicalPositionChange,
                    boolean physicalMobilityHelp, boolean physicalToiletHelp, int physicalTotalMinutes,
                    int cognitiveStimulationMinutes, int cognitiveLifeTogetherMinutes, int cognitiveBehaviorManagementMinutes,
                    int emotionalCommunicationMinutes, boolean householdMealClean, boolean householdPersonalHelp,
                    int householdTotalMinutes, HealthTrend physicalFunctionTrend, HealthTrend mealFunctionTrend,
                    int bowelIncontinenceCount, int urineIncontinenceCount, String specialNotes, String imageUrl) {
        this.patient = patient;
        this.writer = writer;
        this.logDate = logDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.physicalHygiene = physicalHygiene;
        this.physicalBath = physicalBath;
        this.physicalMealHelp = physicalMealHelp;
        this.physicalPositionChange = physicalPositionChange;
        this.physicalMobilityHelp = physicalMobilityHelp;
        this.physicalToiletHelp = physicalToiletHelp;
        this.physicalTotalMinutes = physicalTotalMinutes;
        this.cognitiveStimulationMinutes = cognitiveStimulationMinutes;
        this.cognitiveLifeTogetherMinutes = cognitiveLifeTogetherMinutes;
        this.cognitiveBehaviorManagementMinutes = cognitiveBehaviorManagementMinutes;
        this.emotionalCommunicationMinutes = emotionalCommunicationMinutes;
        this.householdMealClean = householdMealClean;
        this.householdPersonalHelp = householdPersonalHelp;
        this.householdTotalMinutes = householdTotalMinutes;
        this.physicalFunctionTrend = physicalFunctionTrend;
        this.mealFunctionTrend = mealFunctionTrend;
        this.bowelIncontinenceCount = bowelIncontinenceCount;
        this.urineIncontinenceCount = urineIncontinenceCount;
        this.specialNotes = specialNotes;
        this.imageUrl = imageUrl;
    }

    // 수정 비즈니스 로직
    public void update(LocalTime startTime, LocalTime endTime, boolean physicalHygiene, boolean physicalBath,
                       boolean physicalMealHelp, boolean physicalPositionChange, boolean physicalMobilityHelp,
                       boolean physicalToiletHelp, int physicalTotalMinutes, int cognitiveStimulationMinutes,
                       int cognitiveLifeTogetherMinutes, int cognitiveBehaviorManagementMinutes,
                       int emotionalCommunicationMinutes, boolean householdMealClean, boolean householdPersonalHelp,
                       int householdTotalMinutes, HealthTrend physicalFunctionTrend, HealthTrend mealFunctionTrend,
                       int bowelIncontinenceCount, int urineIncontinenceCount, String specialNotes, String imageUrl) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.physicalHygiene = physicalHygiene;
        this.physicalBath = physicalBath;
        this.physicalMealHelp = physicalMealHelp;
        this.physicalPositionChange = physicalPositionChange;
        this.physicalMobilityHelp = physicalMobilityHelp;
        this.physicalToiletHelp = physicalToiletHelp;
        this.physicalTotalMinutes = physicalTotalMinutes;
        this.cognitiveStimulationMinutes = cognitiveStimulationMinutes;
        this.cognitiveLifeTogetherMinutes = cognitiveLifeTogetherMinutes;
        this.cognitiveBehaviorManagementMinutes = cognitiveBehaviorManagementMinutes;
        this.emotionalCommunicationMinutes = emotionalCommunicationMinutes;
        this.householdMealClean = householdMealClean;
        this.householdPersonalHelp = householdPersonalHelp;
        this.householdTotalMinutes = householdTotalMinutes;
        this.physicalFunctionTrend = physicalFunctionTrend;
        this.mealFunctionTrend = mealFunctionTrend;
        this.bowelIncontinenceCount = bowelIncontinenceCount;
        this.urineIncontinenceCount = urineIncontinenceCount;
        this.specialNotes = specialNotes;
        this.imageUrl = imageUrl;
    }
}