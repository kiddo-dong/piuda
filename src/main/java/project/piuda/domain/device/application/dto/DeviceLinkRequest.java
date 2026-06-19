package project.piuda.domain.device.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class DeviceLinkRequest {

    @NotBlank(message = "디바이스 시리얼 번호를 입력해주세요.")
    private String deviceSerial;
}
