package project.piuda.domain.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.device.domain.Device;

import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    Optional<Device> findByDeviceSerial(String deviceSerial);
}