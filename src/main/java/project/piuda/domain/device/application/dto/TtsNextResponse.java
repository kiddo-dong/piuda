package project.piuda.domain.device.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TtsNextResponse {
    private Long messageId;
    private String audioUrl;
}
