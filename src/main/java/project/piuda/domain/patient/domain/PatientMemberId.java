package project.piuda.domain.patient.domain;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@EqualsAndHashCode // 복합키는 equals와 hashCode 구현이 필수입니다.
public class PatientMemberId implements Serializable {

    private Long patientId;
    private Long userId;

    public PatientMemberId(Long patientId, Long userId) {
        this.patientId = patientId;
        this.userId = userId;
    }
}