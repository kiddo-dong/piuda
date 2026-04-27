package project.piuda.device.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.device.domain.Device;

import java.util.Optional;

public interface JpaDeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);
}