package project.piuda.userdevice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.piuda.device.domain.Device;
import project.piuda.userdevice.domain.UserDevice;
import project.piuda.userdevice.domain.UserDeviceRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDeviceRepositoryImpl implements UserDeviceRepository {

    private final JpaUserDeviceRepository jpaRepository;

    @Override
    public UserDevice save(UserDevice userDevice) {
        return jpaRepository.save(userDevice);
    }

    @Override
    public Optional<UserDevice> findByDevice(Device device) {
        return jpaRepository.findByDevice(device);
    }
}