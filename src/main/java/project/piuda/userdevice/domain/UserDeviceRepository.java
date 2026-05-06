package project.piuda.userdevice.domain;

import project.piuda.device.domain.Device;
import project.piuda.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository {

    UserDevice save(UserDevice userDevice);

    Optional<UserDevice> findByDevice(Device device);

    List<UserDevice> findAllByUser(User user);

    Optional<UserDevice> findByUserAndDevice(User user, Device device);

    void delete(UserDevice userDevice);
}
