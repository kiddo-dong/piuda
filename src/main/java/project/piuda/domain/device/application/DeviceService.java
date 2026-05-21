package project.piuda.domain.device.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional
    public Long registerDevice(DeviceRegisterRequest request) {
        if(deviceRepository.findByDeviceSerial(request.getDeviceSerial()).isPresent()) {
            throw new IllegalArgumentException("이미 기기 마스터에 등록된 시리얼 번호입니다.");
        }
        Device device = Device.builder().deviceSerial(request.getDeviceSerial()).build();
        return deviceRepository.save(device).getId();
    }
}