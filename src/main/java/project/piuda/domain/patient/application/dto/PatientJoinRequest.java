package project.piuda.domain.patient.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatientJoinRequest {

    @NotBlank(message = "초대 코드를 입력해주세요.")
    private String inviteCode;

    private String relationship;
}
