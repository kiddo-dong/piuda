package project.piuda.domain.patient.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.patient.domain.Gender;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PatientRegisterRequest {
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private String dementiaStage;
}