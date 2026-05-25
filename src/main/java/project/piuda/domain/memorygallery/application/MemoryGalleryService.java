package project.piuda.domain.memorygallery.application;

import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.device.domain.VoiceRecord;
import project.piuda.domain.device.domain.VoiceRecordRepository;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryItem;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryUploadRequest;
import project.piuda.domain.memorygallery.domain.MemoryGallery;
import project.piuda.domain.memorygallery.domain.MemoryGalleryRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryGalleryService {

    private final DailyLogRepository dailyLogRepository;
    private final VoiceRecordRepository voiceRecordRepository;
    private final MemoryGalleryRepository memoryGalleryRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Transactional
    public void uploadPhoto(Long patientId, String userEmail, MemoryGalleryUploadRequest request) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));
        User writer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        memoryGalleryRepository.save(MemoryGallery.builder()
                .patient(patient)
                .writer(writer)
                .imageUrl(request.getImageUrl())
                .memo(request.getMemo())
                .build());
    }

    // 환자 기준으로 이미지(DailyLog + 직접 업로드) + 음성(VoiceRecord) 통합 조회 (최신순)
    public List<MemoryGalleryItem> getGallery(Long patientId) {
        List<MemoryGalleryItem> items = new ArrayList<>();

        for (DailyLog log : dailyLogRepository.findByPatientIdAndImageUrlIsNotNullOrderByLogDateDesc(patientId)) {
            LocalDateTime recordedAt = log.getLogDate().atTime(log.getStartTime());
            items.add(MemoryGalleryItem.ofImage(log.getImageUrl(), recordedAt, log.getWriter().getName()));
        }

        for (MemoryGallery gallery : memoryGalleryRepository.findAllByPatientIdOrderByUploadedAtDesc(patientId)) {
            items.add(MemoryGalleryItem.ofImage(gallery.getImageUrl(), gallery.getUploadedAt(), gallery.getWriter().getName()));
        }

        for (VoiceRecord voice : voiceRecordRepository.findAllByPatientIdOrderByRecordedAtDesc(patientId)) {
            items.add(MemoryGalleryItem.ofAudio(voice.getAudioUrl(), voice.getRecordedAt()));
        }

        items.sort(Comparator.comparing(MemoryGalleryItem::getRecordedAt).reversed());

        return items;
    }
}
