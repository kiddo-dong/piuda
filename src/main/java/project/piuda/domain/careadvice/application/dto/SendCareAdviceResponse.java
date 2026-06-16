package project.piuda.domain.careadvice.application.dto;

import lombok.Getter;

@Getter
public class SendCareAdviceResponse {

    private final CareAdviceMessageResponse userMessage;
    private final CareAdviceMessageResponse assistantMessage;
    private final boolean ragUsed;

    public SendCareAdviceResponse(CareAdviceMessageResponse userMessage,
                                  CareAdviceMessageResponse assistantMessage,
                                  boolean ragUsed) {
        this.userMessage = userMessage;
        this.assistantMessage = assistantMessage;
        this.ragUsed = ragUsed;
    }
}
