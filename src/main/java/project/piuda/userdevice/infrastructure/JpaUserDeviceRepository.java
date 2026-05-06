package project.piuda.userdevice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.device.domain.Device;
import project.piuda.user.domain.User;
import project.piuda.userdevice.domain.UserDevice;

import java.util.List;
import java.util.Optional;

public interface JpaUserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByDevice(Device device);

    List<UserDevice> findAllByUser(User user);

    Optional<UserDevice> findByUserAndDevice(User user, Device device);
}
