package project.piuda.domain.patient.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.patient.domain.DementiaStage;
import project.piuda.domain.patient.domain.Gender;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class PatientUpdateRequest {

    @NotBlank(message = "환자 이름을 입력해주세요.")
    private String name;

    @NotNull(message = "생년월일을 입력해주세요.")
    private LocalDate birthDate;

    @NotNull(message = "성별을 선택해주세요.")
    private Gender gender;

    @NotNull(message = "치매 단계를 선택해주세요.")
    private DementiaStage dementiaStage;
}
