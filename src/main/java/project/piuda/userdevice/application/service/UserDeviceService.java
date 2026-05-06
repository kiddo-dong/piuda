package project.piuda.userdevice.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.device.application.dto.DeviceResponse;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;
import project.piuda.user.domain.User;
import project.piuda.user.domain.UserRepository;
import project.piuda.userdevice.domain.UserDevice;
import project.piuda.userdevice.domain.UserDeviceRepository;

import java.util.List;

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

    public List<DeviceResponse> getMyDevices(Long userId) {
        User user = getUser(userId);

        return userDeviceRepository.findAllByUser(user).stream()
                .map(UserDevice::getDevice)
                .map(DeviceResponse::from)
                .toList();
    }

    public DeviceResponse getMyDevice(Long userId, Long devicePk) {
        User user = getUser(userId);
        Device device = getDevice(devicePk);

        userDeviceRepository.findByUserAndDevice(user, device)
                .orElseThrow(() -> new RuntimeException("연결되지 않은 디바이스"));

        return DeviceResponse.from(device);
    }

    public void disconnect(Long userId, Long devicePk) {
        User user = getUser(userId);
        Device device = getDevice(devicePk);

        UserDevice userDevice = userDeviceRepository.findByUserAndDevice(user, device)
                .orElseThrow(() -> new RuntimeException("연결되지 않은 디바이스"));

        userDeviceRepository.delete(userDevice);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("유저 없음"));
    }

    private Device getDevice(Long devicePk) {
        return deviceRepository.findById(devicePk)
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));
    }
}
