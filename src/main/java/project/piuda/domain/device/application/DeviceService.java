package project.piuda.domain.device.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.device.application.dto.DeviceRegisterRequest;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceRepository;
import project.piuda.domain.device.domain.VoiceRecord;
import project.piuda.domain.device.domain.VoiceRecordRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.global.infrastructure.S3UploadService;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final VoiceRecordRepository voiceRecordRepository;
    private final PatientRepository patientRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Long registerDevice(DeviceRegisterRequest request) {
        if (deviceRepository.findByDeviceSerial(request.getDeviceSerial()).isPresent()) {
            throw new IllegalArgumentException("이미 기기 마스터에 등록된 시리얼 번호입니다.");
        }
        Device device = Device.builder().deviceSerial(request.getDeviceSerial()).build();
        return deviceRepository.save(device).getId();
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
}
