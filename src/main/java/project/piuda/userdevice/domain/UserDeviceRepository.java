package project.piuda.userdevice.domain;

import project.piuda.device.domain.Device;

import java.util.Optional;

public interface UserDeviceRepository {

    UserDevice save(UserDevice userDevice);

    Optional<UserDevice> findByDevice(Device device);
}