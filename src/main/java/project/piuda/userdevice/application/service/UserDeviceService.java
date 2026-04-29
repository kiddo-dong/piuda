package project.piuda.userdevice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;
import project.piuda.user.domain.User;
import project.piuda.user.domain.UserRepository;
import project.piuda.userdevice.domain.UserDevice;
import project.piuda.userdevice.domain.UserDeviceRepository;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public void connect(Long userId, String deviceId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));

        userDeviceRepository.findByDevice(device)
                .ifPresent(userDevice -> {
                    throw new RuntimeException("이미 연결된 디바이스");
                });

        userDeviceRepository.save(new UserDevice(user, device));
    }
}
