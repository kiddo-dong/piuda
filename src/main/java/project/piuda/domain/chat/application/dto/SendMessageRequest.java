package project.piuda.domain.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.chat.domain.MessageType;

@Getter
@NoArgsConstructor
public class SendMessageRequest {

    private MessageType messageType;

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    @Size(max = 2000, message = "메시지는 2000자 이하로 입력해주세요.")
    private String content;
}
