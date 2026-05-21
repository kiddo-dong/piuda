package project.piuda.domain.device.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeviceRegisterRequest {
    private String deviceSerial;
}