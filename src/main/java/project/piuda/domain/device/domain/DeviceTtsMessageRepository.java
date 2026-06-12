package project.piuda.domain.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTtsMessageRepository extends JpaRepository<DeviceTtsMessage, Long> {
    Optional<DeviceTtsMessage> findFirstByDevice_DeviceSerialAndPlayedFalseOrderByCreatedAtAsc(String deviceSerial);
}
