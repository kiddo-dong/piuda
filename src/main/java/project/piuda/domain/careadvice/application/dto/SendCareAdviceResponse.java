package project.piuda.domain.careadvice.application.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SendCareAdviceResponse {

    private final CareAdviceMessageResponse userMessage;
    private final CareAdviceMessageResponse assistantMessage;
}
