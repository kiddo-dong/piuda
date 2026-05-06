package project.piuda.userdevice.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import project.piuda.device.domain.Device;
import project.piuda.user.domain.User;
import project.piuda.userdevice.domain.UserDevice;
import project.piuda.userdevice.domain.UserDeviceRepository;

import java.util.List;
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

    @Override
    public List<UserDevice> findAllByUser(User user) {
        return jpaRepository.findAllByUser(user);
    }

    @Override
    public Optional<UserDevice> findByUserAndDevice(User user, Device device) {
        return jpaRepository.findByUserAndDevice(user, device);
    }

    @Override
    public void delete(UserDevice userDevice) {
        jpaRepository.delete(userDevice);
    }
}
