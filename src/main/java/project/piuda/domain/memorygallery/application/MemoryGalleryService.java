package project.piuda.domain.memorygallery.application;

import project.piuda.domain.dailylog.domain.DailyLog;
import project.piuda.domain.dailylog.domain.DailyLogRepository;
import project.piuda.domain.device.domain.VoiceRecord;
import project.piuda.domain.device.domain.VoiceRecordRepository;
import project.piuda.domain.memorygallery.application.dto.MemoryGalleryItem;
import project.piuda.domain.memorygallery.domain.MemoryGallery;
import project.piuda.domain.memorygallery.domain.MemoryGalleryRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public void uploadPhoto(Long patientId, String userEmail, MultipartFile image, String memo) throws IOException {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User writer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        validatePatientAccess(patient, writer);

        String imageUrl = s3UploadService.upload(image, "memory-gallery");

        memoryGalleryRepository.save(MemoryGallery.builder()
                .patient(patient)
                .writer(writer)
                .imageUrl(imageUrl)
                .memo(memo)
                .build());
    }

    public List<MemoryGalleryItem> getGallery(Long patientId, String userEmail) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        validatePatientAccess(patient, user);
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

    @Transactional
    public void deletePhoto(Long galleryId, String userEmail) {
        MemoryGallery gallery = memoryGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 갤러리 항목입니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));

        validatePatientAccess(gallery.getPatient(), user);
        memoryGalleryRepository.delete(gallery);
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
