package project.piuda.domain.carejudgment.application.dto;

import lombok.Getter;
import project.piuda.domain.carejudgment.domain.CareJudgmentLog;
import project.piuda.domain.carejudgment.domain.JudgmentCategory;
import project.piuda.domain.carejudgment.domain.UrgencyLevel;

import java.time.LocalDateTime;

@Getter
public class CareJudgmentLogResponse {

    private final Long id;
    private final Long patientId;
    private final String writerName;
    private final JudgmentCategory category;
    private final String situation;
    private final String action;
    private final String rationale;
    private final UrgencyLevel urgency;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CareJudgmentLogResponse(CareJudgmentLog log) {
        this.id = log.getId();
        this.patientId = log.getPatient().getId();
        this.writerName = log.getWriter().getName();
        this.category = log.getCategory();
        this.situation = log.getSituation();
        this.action = log.getAction();
        this.rationale = log.getRationale();
        this.urgency = log.getUrgency();
        this.createdAt = log.getCreatedAt();
        this.updatedAt = log.getUpdatedAt();
    }
}
