package project.piuda.domain.patient.application.dto;

import lombok.Getter;
import lombok.Setter;
import project.piuda.domain.patient.domain.Gender;

import java.time.LocalDate;

@Getter
@Setter // MapStruct가 사용하기 위해 Setter 오픈
public class PatientResponse {
    private Long id;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private String dementiaStage;
    private String deviceSerial; // 디바이스가 연동된 경우 시리얼 번호 포함, 없으면 null
}