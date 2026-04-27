package project.piuda.device.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public String register(String deviceId) {

        return deviceRepository.findByDeviceId(deviceId)
                .map(Device::getDeviceKey)
                .orElseGet(() -> {
                    String key = UUID.randomUUID().toString();
                    Device device = new Device(deviceId, key);
                    deviceRepository.save(device);
                    return key;
                });
    }

    public Device validate(String deviceId, String deviceKey) {

        Device device = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));

        if (!device.getDeviceKey().equals(deviceKey)) {
            throw new RuntimeException("디바이스 인증 실패");
        }

        return device;
    }
}