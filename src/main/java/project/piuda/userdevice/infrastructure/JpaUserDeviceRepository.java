package project.piuda.userdevice.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.device.domain.Device;
import project.piuda.userdevice.domain.UserDevice;

import java.util.Optional;

public interface JpaUserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByDevice(Device device);
}