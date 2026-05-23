package project.piuda.domain.memorybook.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemoryBookRequest {
    private String bloodType;
    private int longTermCareGrade;
    private String dementiaType;
    private String comorbidities;
    private String contraindications;
    private String medicationInfo;
    private String prnMedicationInfo;
    private String primaryDoctorInfo;
    private String likes;
    private String dislikes;
    private String soothingWords;
    private String ineffectiveWords;
    private String sundowningInfo;
    private String repetitiveBehaviors;
    private String wanderingRoute;
    private String emergencyContacts;
    private String preferredHospital;
    private String specialNotes; // 특이사항 추가
}