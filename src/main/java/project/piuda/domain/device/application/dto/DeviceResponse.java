package project.piuda.domain.device.application.dto;

import lombok.Getter;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceStatus;

import java.time.LocalDateTime;

@Getter
public class DeviceResponse {
    private final Long id;
    private final String deviceSerial;
    private final DeviceStatus deviceStatus;
    private final LocalDateTime purchasedAt;
    private final LocalDateTime lastConnectedAt;

    public DeviceResponse(Device device) {
        this.id                = device.getId();
        this.deviceSerial      = device.getDeviceSerial();
        this.deviceStatus      = device.getDeviceStatus();
        this.purchasedAt       = device.getPurchasedAt();
        this.lastConnectedAt   = device.getLastConnectedAt();
    }
}
