package project.piuda.domain.patient.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatientJoinRequest {
    private String inviteCode;
    private String relationship;
}
