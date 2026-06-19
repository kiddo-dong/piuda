package project.piuda.domain.careadvice.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CareAdviceMessageRequest {

    @NotBlank(message = "메시지 내용을 입력해주세요.")
    @Size(max = 1000, message = "메시지는 1000자 이하로 입력해주세요.")
    private String content;
}
