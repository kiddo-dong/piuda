package project.piuda.device.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DeviceRepository {

    private final JpaDeviceRepository jpaDeviceRepository;

    @Override
    public Device save(Device device) {
        return jpaDeviceRepository.save(device);
    }

    @Override
    public Optional<Device> findByDeviceId(String deviceId) {
        return jpaDeviceRepository.findByDeviceId(deviceId);
    }
}
