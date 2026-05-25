package project.piuda.domain.careadvice.application.dto;

import lombok.Getter;
import project.piuda.domain.careadvice.domain.CareAdviceSession;

import java.time.LocalDateTime;

@Getter
public class CareAdviceSessionResponse {

    private final Long sessionId;
    private final Long patientId;
    private final LocalDateTime createdAt;

    public CareAdviceSessionResponse(CareAdviceSession session) {
        this.sessionId = session.getId();
        this.patientId = session.getPatient().getId();
        this.createdAt = session.getCreatedAt();
    }
}
