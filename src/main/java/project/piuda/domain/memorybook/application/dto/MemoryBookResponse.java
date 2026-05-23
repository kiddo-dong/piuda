package project.piuda.domain.memorybook.application.dto;

import project.piuda.domain.memorybook.domain.MemoryBook;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemoryBookResponse {
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
    private final String specialNotes; // 특이사항 추가
    private final LocalDateTime updatedAt;

    public MemoryBookResponse(MemoryBook memoryBook) {
        this.id = memoryBook.getId();
        this.patientId = memoryBook.getPatient().getId();
        this.bloodType = memoryBook.getBloodType();
        this.longTermCareGrade = memoryBook.getLongTermCareGrade();
        this.dementiaType = memoryBook.getDementiaType();
        this.comorbidities = memoryBook.getComorbidities();
        this.contraindications = memoryBook.getContraindications();
        this.medicationInfo = memoryBook.getMedicationInfo();
        this.prnMedicationInfo = memoryBook.getPrnMedicationInfo();
        this.primaryDoctorInfo = memoryBook.getPrimaryDoctorInfo();
        this.likes = memoryBook.getLikes();
        this.dislikes = memoryBook.getDislikes();
        this.soothingWords = memoryBook.getSoothingWords();
        this.ineffectiveWords = memoryBook.getIneffectiveWords();
        this.sundowningInfo = memoryBook.getSundowningInfo();
        this.repetitiveBehaviors = memoryBook.getRepetitiveBehaviors();
        this.wanderingRoute = memoryBook.getWanderingRoute();
        this.emergencyContacts = memoryBook.getEmergencyContacts();
        this.preferredHospital = memoryBook.getPreferredHospital();
        this.specialNotes = memoryBook.getSpecialNotes();
        this.updatedAt = memoryBook.getUpdatedAt();
    }
}