package project.piuda.domain.memorygallery.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.device.domain.VoiceRecord;
import project.piuda.domain.device.domain.VoiceRecordRepository;
import project.piuda.domain.memorygallery.application.dto.AudioGalleryItem;
import project.piuda.domain.memorygallery.application.dto.PhotoGalleryItem;
import project.piuda.domain.memorygallery.domain.MemoryGallery;
import project.piuda.domain.memorygallery.domain.MemoryGalleryRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryGalleryService {

    private final MemoryGalleryRepository memoryGalleryRepository;
    private final VoiceRecordRepository voiceRecordRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public void uploadPhoto(Long patientId, String userEmail, MultipartFile image, String memo) throws IOException {
        Patient patient = getPatient(patientId);
        User writer = getUser(userEmail);
        validatePatientAccess(patient, writer);

        if (image == null || image.isEmpty()) {
            throw new BusinessException("이미지 파일이 필요합니다.");
        }
        String imageUrl = s3UploadService.upload(image, "memory-gallery");
        try {
            memoryGalleryRepository.save(MemoryGallery.builder()
                    .patient(patient)
                    .writer(writer)
                    .imageUrl(imageUrl)
                    .memo(memo)
                    .build());
        } catch (Exception e) {
            s3UploadService.delete(imageUrl);
            throw e;
        }
    }

    public List<PhotoGalleryItem> getPhotoGallery(Long patientId, String userEmail) {
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, getUser(userEmail));

        List<PhotoGalleryItem> items = new ArrayList<>();

        for (MemoryGallery gallery : memoryGalleryRepository.findAllByPatientIdOrderByUploadedAtDesc(patientId)) {
            items.add(PhotoGalleryItem.ofGalleryImage(
                    gallery.getId(),
                    gallery.getImageUrl(),
                    gallery.getUploadedAt(),
                    gallery.getWriter().getName(),
                    gallery.getMemo()
            ));
        }

        items.sort(Comparator.comparing(PhotoGalleryItem::getRecordedAt).reversed());
        return items;
    }

    public List<AudioGalleryItem> getAudioGallery(Long patientId, String userEmail) {
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, getUser(userEmail));

        List<AudioGalleryItem> items = new ArrayList<>();
        for (VoiceRecord voice : voiceRecordRepository.findAllByPatientIdOrderByRecordedAtDesc(patientId)) {
            items.add(AudioGalleryItem.of(voice.getId(), voice.getAudioUrl(), voice.getRecordedAt()));
        }
        return items;
    }

    @Transactional
    public void deletePhoto(Long galleryId, String userEmail) {
        MemoryGallery gallery = memoryGalleryRepository.findById(galleryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 갤러리 항목입니다."));
        validatePatientAccess(gallery.getPatient(), getUser(userEmail));
        memoryGalleryRepository.delete(gallery);
    }

    @Transactional
    public void deleteAudio(Long audioId, String userEmail) {
        VoiceRecord voice = voiceRecordRepository.findById(audioId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 음성 기록입니다."));
        validatePatientAccess(voice.getPatient(), getUser(userEmail));
        voiceRecordRepository.delete(voice);
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
