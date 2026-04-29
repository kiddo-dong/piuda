package project.piuda.audio.application;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.audio.domain.Audio;
import project.piuda.audio.domain.AudioRepository;
import project.piuda.device.domain.Device;
import project.piuda.device.domain.DeviceRepository;

@Service
@RequiredArgsConstructor
public class AudioService {

    private final AudioRepository audioRepository;
    private final DeviceRepository deviceRepository;

    public void save(Long deviceId) {

        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("디바이스 없음"));

        Audio audio = Audio.builder()
                .device(device)
                .filePath("test-path") // 나중에 파일 저장으로 변경
                .build();

        audioRepository.save(audio);
    }
}
