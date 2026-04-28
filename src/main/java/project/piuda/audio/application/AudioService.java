package project.piuda.audio.application;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.audio.domain.Audio;
import project.piuda.audio.domain.AudioRepository;
import project.piuda.device.domain.Device;

@Service
@RequiredArgsConstructor
public class AudioService {

    private final AudioRepository audioRepository;

    public void save(Device device) {

        Audio audio = Audio.builder()
                .device(device)
                .filePath("test-path") // 나중에 파일 저장으로 변경
                .build();

        audioRepository.save(audio);
    }
}