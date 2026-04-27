package project.piuda.device.domain;

import java.util.Optional;

public interface DeviceRepository {

    Device save(Device device);

    Optional<Device> findByDeviceId(String deviceId);
}