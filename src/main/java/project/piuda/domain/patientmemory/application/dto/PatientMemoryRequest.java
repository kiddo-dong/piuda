package project.piuda.domain.patientmemory.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatientMemoryRequest {
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
    private String specialNotes;
}
