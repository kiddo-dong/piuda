package project.piuda.domain.careadvice.application.dto;

import lombok.Getter;
import project.piuda.domain.careadvice.domain.CareAdviceMessage;
import project.piuda.domain.careadvice.domain.MessageRole;

import java.time.LocalDateTime;

@Getter
public class CareAdviceMessageResponse {

    private final Long messageId;
    private final MessageRole role;
    private final String content;
    private final LocalDateTime createdAt;

    public CareAdviceMessageResponse(CareAdviceMessage message) {
        this.messageId = message.getId();
        this.role = message.getRole();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }
}
