package project.piuda.domain.memorybook.domain;

import project.piuda.domain.patient.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memory_books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemoryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memory_book_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true)
    private Patient patient;

    // 1. 기본/신상
    private String bloodType;
    private int longTermCareGrade;

    // 2. 질환/병력
    private String dementiaType;
    private String comorbidities;

    @Column(columnDefinition = "TEXT")
    private String contraindications; // 알레르기, 금기 식품 등

    // 3. 복약 정보
    @Column(columnDefinition = "TEXT")
    private String medicationInfo;

    @Column(columnDefinition = "TEXT")
    private String prnMedicationInfo;

    private String primaryDoctorInfo;

    // 4. 생활 성향 및 감정 패턴
    @Column(columnDefinition = "TEXT")
    private String likes;

    @Column(columnDefinition = "TEXT")
    private String dislikes;

    @Column(columnDefinition = "TEXT")
    private String soothingWords;

    @Column(columnDefinition = "TEXT")
    private String ineffectiveWords;

    private String sundowningInfo;

    @Column(columnDefinition = "TEXT")
    private String repetitiveBehaviors;

    @Column(columnDefinition = "TEXT")
    private String wanderingRoute;

    // 5. 응급 정보
    @Column(columnDefinition = "TEXT")
    private String emergencyContacts;

    private String preferredHospital;

    // 6. 기타 특이사항 ★ 추가
    @Column(columnDefinition = "TEXT")
    private String specialNotes;

    private LocalDateTime updatedAt;

    @Builder
    public MemoryBook(Patient patient, String bloodType, int longTermCareGrade, String dementiaType,
                      String comorbidities, String contraindications, String medicationInfo,
                      String prnMedicationInfo, String primaryDoctorInfo, String likes, String dislikes,
                      String soothingWords, String ineffectiveWords, String sundowningInfo,
                      String repetitiveBehaviors, String wanderingRoute, String emergencyContacts,
                      String preferredHospital, String specialNotes) {
        this.patient = patient;
        this.bloodType = bloodType;
        this.longTermCareGrade = longTermCareGrade;
        this.dementiaType = dementiaType;
        this.comorbidities = comorbidities;
        this.contraindications = contraindications;
        this.medicationInfo = medicationInfo;
        this.prnMedicationInfo = prnMedicationInfo;
        this.primaryDoctorInfo = primaryDoctorInfo;
        this.likes = likes;
        this.dislikes = dislikes;
        this.soothingWords = soothingWords;
        this.ineffectiveWords = ineffectiveWords;
        this.sundowningInfo = sundowningInfo;
        this.repetitiveBehaviors = repetitiveBehaviors;
        this.wanderingRoute = wanderingRoute;
        this.emergencyContacts = emergencyContacts;
        this.preferredHospital = preferredHospital;
        this.specialNotes = specialNotes; // ★ 반영
        this.updatedAt = LocalDateTime.now();
    }

    // === 비즈니스 수정 로직 (특이사항 인자 추가 반영) ===
    public void updateContent(String bloodType, int longTermCareGrade, String dementiaType,
                              String comorbidities, String contraindications, String medicationInfo,
                              String prnMedicationInfo, String primaryDoctorInfo, String likes, String dislikes,
                              String soothingWords, String ineffectiveWords, String sundowningInfo,
                              String repetitiveBehaviors, String wanderingRoute, String emergencyContacts,
                              String preferredHospital, String specialNotes) {
        this.bloodType = bloodType;
        this.longTermCareGrade = longTermCareGrade;
        this.dementiaType = dementiaType;
        this.comorbidities = comorbidities;
        this.contraindications = contraindications;
        this.medicationInfo = medicationInfo;
        this.prnMedicationInfo = prnMedicationInfo;
        this.primaryDoctorInfo = primaryDoctorInfo;
        this.likes = likes;
        this.dislikes = dislikes;
        this.soothingWords = soothingWords;
        this.ineffectiveWords = ineffectiveWords;
        this.sundowningInfo = sundowningInfo;
        this.repetitiveBehaviors = repetitiveBehaviors;
        this.wanderingRoute = wanderingRoute;
        this.emergencyContacts = emergencyContacts;
        this.preferredHospital = preferredHospital;
        this.specialNotes = specialNotes; // ★ 반영
        this.updatedAt = LocalDateTime.now();
    }
}