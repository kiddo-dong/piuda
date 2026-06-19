package project.piuda.domain.chat.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.chat.domain.MessageType;

@Getter
@NoArgsConstructor
public class SendMessageRequest {
    private MessageType messageType;
    private String content;
}
