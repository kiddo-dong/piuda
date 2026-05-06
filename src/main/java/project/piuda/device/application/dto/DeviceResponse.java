package project.piuda.device.application.dto;

import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceStatus;

public record DeviceResponse(
        Long id,
        String deviceId,
        DeviceStatus status
) {
    public static DeviceResponse from(Device device) {
        return new DeviceResponse(
                device.getId(),
                device.getDeviceId(),
                device.getStatus()
        );
    }
}
