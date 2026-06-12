package project.piuda.domain.device.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;
import project.piuda.domain.device.application.dto.TtsNextResponse;
import project.piuda.domain.device.application.dto.TtsQueueRequest;
import project.piuda.domain.device.domain.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final VoiceRecordRepository voiceRecordRepository;
    private final DeviceTtsMessageRepository ttsMessageRepository;
    private final PatientRepository patientRepository;
    private final S3UploadService s3UploadService;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.openai.api-key}")
    private String openAiApiKey;

    @Transactional
    public Long registerDevice(DeviceRegisterRequest request) {
        return deviceRepository.findByDeviceSerial(request.getDeviceSerial())
                .map(Device::getId)
                .orElseGet(() -> deviceRepository.save(
                        Device.builder().deviceSerial(request.getDeviceSerial()).build()).getId());
    }

    // ESP32 마이크 녹음 수신 → 디바이스로 환자 식별 → 환자 기준으로 음성 데이터 저장
    @Transactional
    public void saveVoiceRecord(String deviceSerial, MultipartFile audioFile) throws IOException {
        Patient patient = patientRepository.findByDeviceDeviceSerial(deviceSerial)
                .orElseThrow(() -> new IllegalArgumentException("해당 시리얼의 디바이스에 연동된 환자가 없습니다."));

        String audioUrl = s3UploadService.uploadAudio(audioFile, "voice-records");

        VoiceRecord voiceRecord = VoiceRecord.builder()
                .patient(patient)
                .audioUrl(audioUrl)
                .recordedAt(LocalDateTime.now())
                .build();

        voiceRecordRepository.save(voiceRecord);
    }

    // 앱 → 서버: TTS 텍스트를 기기에 전송 (OpenAI TTS → S3 → DB 큐잉)
    @Transactional
    public void queueTts(String deviceSerial, TtsQueueRequest request) throws IOException {
        Device device = deviceRepository.findByDeviceSerial(deviceSerial)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 디바이스입니다."));

        byte[] audioBytes = generateTtsAudio(request.getText());
        String audioUrl   = s3UploadService.uploadAudioBytes(audioBytes, "tts-messages");

        ttsMessageRepository.save(DeviceTtsMessage.builder()
                .device(device)
                .text(request.getText())
                .audioUrl(audioUrl)
                .build());
    }

    // ESP32 → 서버: 다음 미재생 TTS 메시지 조회
    public Optional<TtsNextResponse> getNextTts(String deviceSerial) {
        return ttsMessageRepository
                .findFirstByDevice_DeviceSerialAndPlayedFalseOrderByCreatedAtAsc(deviceSerial)
                .map(m -> new TtsNextResponse(m.getId(), m.getAudioUrl()));
    }

    // ESP32 → 서버: TTS 재생 완료 ACK
    @Transactional
    public void ackTts(String deviceSerial, Long messageId) {
        DeviceTtsMessage msg = ttsMessageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 TTS 메시지입니다."));
        if (!msg.getDevice().getDeviceSerial().equals(deviceSerial)) {
            throw new NotFoundException("해당 디바이스의 메시지가 아닙니다.");
        }
        msg.markPlayed();
    }

    private byte[] generateTtsAudio(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        Map<String, Object> body = Map.of(
                "model",           "tts-1",
                "input",           text,
                "voice",           "alloy",
                "response_format", "wav"
        );

        ResponseEntity<byte[]> response = restTemplate.exchange(
                "https://api.openai.com/v1/audio/speech",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                byte[].class
        );

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("OpenAI TTS 생성 실패");
        }
        return response.getBody();
    }
}
