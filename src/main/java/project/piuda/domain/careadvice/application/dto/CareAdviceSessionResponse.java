package project.piuda.domain.careadvice.application.dto;

import lombok.Getter;
import project.piuda.domain.careadvice.domain.CareAdviceSession;

import java.time.LocalDateTime;

@Getter
public class CareAdviceSessionResponse {

    private final Long sessionId;
    private final Long patientId;
    private final String preview;   // 첫 사용자 메시지 앞부분 (프론트 세션 제목 생성용)
    private final LocalDateTime createdAt;

    public CareAdviceSessionResponse(CareAdviceSession session, String preview) {
        this.sessionId = session.getId();
        this.patientId = session.getPatient().getId();
        this.preview = preview;
        this.createdAt = session.getCreatedAt();
    }
}
