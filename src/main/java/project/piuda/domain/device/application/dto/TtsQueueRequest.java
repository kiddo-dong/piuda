package project.piuda.domain.device.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class TtsQueueRequest {

    @NotBlank(message = "TTS 텍스트를 입력해주세요.")
    @Size(max = 500, message = "TTS 텍스트는 500자 이하로 입력해주세요.")
    private String text;
}
