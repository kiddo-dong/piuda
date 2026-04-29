package project.piuda.device.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;
import project.piuda.global.security.principal.DevicePrincipal;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;

    public String register(String deviceId) {

        deviceRepository.findByDeviceId(deviceId)
                .ifPresent(device -> {
                    throw new RuntimeException("이미 등록된 디바이스");
                });

        String key = createDeviceSecret();
        Device device = new Device(deviceId, passwordEncoder.encode(key));
        deviceRepository.save(device);
        return key;
    }

    public DevicePrincipal validate(String deviceId, String deviceKey) {
        Device device = getByDeviceId(deviceId);

        if (!device.isActive()) {
            throw new RuntimeException("비활성화된 디바이스");
        }

        if (!passwordEncoder.matches(deviceKey, device.getDeviceKeyHash())) {
            throw new RuntimeException("디바이스 인증 실패");
        }

        return new DevicePrincipal(device.getId(), device.getDeviceId());
    }

    public Device getByDeviceId(String deviceId) {
        return deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));
    }

    private String createDeviceSecret() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
