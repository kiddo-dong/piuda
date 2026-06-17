package project.piuda.domain.admin.application.dto;

import lombok.Getter;
import project.piuda.domain.device.domain.Device;

import java.time.LocalDateTime;

@Getter
public class AdminDeviceResponse {
    private final Long id;
    private final String deviceSerial;
    private final String status;
    private final LocalDateTime purchasedAt;
    private final LocalDateTime lastConnectedAt;
    private final String linkedPatientName;

    public AdminDeviceResponse(Device device, String linkedPatientName) {
        this.id = device.getId();
        this.deviceSerial = device.getDeviceSerial();
        this.status = device.getDeviceStatus() != null ? device.getDeviceStatus().name() : null;
        this.purchasedAt = device.getPurchasedAt();
        this.lastConnectedAt = device.getLastConnectedAt();
        this.linkedPatientName = linkedPatientName;
    }
}
