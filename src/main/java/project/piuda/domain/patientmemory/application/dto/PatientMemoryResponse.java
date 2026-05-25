package project.piuda.domain.patientmemory.application.dto;

import project.piuda.domain.patientmemory.domain.PatientMemory;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PatientMemoryResponse {
    private final Long id;
    private final Long patientId;
    private final String bloodType;
    private final int longTermCareGrade;
    private final String dementiaType;
    private final String comorbidities;
    private final String contraindications;
    private final String medicationInfo;
    private final String prnMedicationInfo;
    private final String primaryDoctorInfo;
    private final String likes;
    private final String dislikes;
    private final String soothingWords;
    private final String ineffectiveWords;
    private final String sundowningInfo;
    private final String repetitiveBehaviors;
    private final String wanderingRoute;
    private final String emergencyContacts;
    private final String preferredHospital;
    private final String specialNotes;
    private final LocalDateTime updatedAt;

    public PatientMemoryResponse(PatientMemory patientMemory) {
        this.id = patientMemory.getId();
        this.patientId = patientMemory.getPatient().getId();
        this.bloodType = patientMemory.getBloodType();
        this.longTermCareGrade = patientMemory.getLongTermCareGrade();
        this.dementiaType = patientMemory.getDementiaType();
        this.comorbidities = patientMemory.getComorbidities();
        this.contraindications = patientMemory.getContraindications();
        this.medicationInfo = patientMemory.getMedicationInfo();
        this.prnMedicationInfo = patientMemory.getPrnMedicationInfo();
        this.primaryDoctorInfo = patientMemory.getPrimaryDoctorInfo();
        this.likes = patientMemory.getLikes();
        this.dislikes = patientMemory.getDislikes();
        this.soothingWords = patientMemory.getSoothingWords();
        this.ineffectiveWords = patientMemory.getIneffectiveWords();
        this.sundowningInfo = patientMemory.getSundowningInfo();
        this.repetitiveBehaviors = patientMemory.getRepetitiveBehaviors();
        this.wanderingRoute = patientMemory.getWanderingRoute();
        this.emergencyContacts = patientMemory.getEmergencyContacts();
        this.preferredHospital = patientMemory.getPreferredHospital();
        this.specialNotes = patientMemory.getSpecialNotes();
        this.updatedAt = patientMemory.getUpdatedAt();
    }
}
