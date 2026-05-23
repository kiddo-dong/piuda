package project.piuda.domain.memorybook.application;

import project.piuda.domain.memorybook.application.dto.MemoryBookRequest;
import project.piuda.domain.memorybook.application.dto.MemoryBookResponse;
import project.piuda.domain.memorybook.domain.MemoryBook;
import project.piuda.domain.memorybook.domain.MemoryBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemoryBookService {

    private final MemoryBookRepository memoryBookRepository;

    // 1. 메모리 북 단건 조회
    public MemoryBookResponse getMemoryBook(Long patientId) {
        MemoryBook memoryBook = memoryBookRepository.findByPatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 메모리 북이 존재하지 않습니다."));
        return new MemoryBookResponse(memoryBook);
    }

    // 2. 메모리 북 내용 수정/업데이트
    @Transactional
    public void updateMemoryBook(Long patientId, MemoryBookRequest request) {
        MemoryBook memoryBook = memoryBookRepository.findByPatientId(patientId)
                .orElseThrow(() -> new IllegalArgumentException("해당 환자의 메모리 북이 존재하지 않습니다."));

        // 엔티티 내부의 도메인 로직을 한 장소에서 실행 (DDD 스타일)
        memoryBook.updateContent(
                request.getBloodType(),
                request.getLongTermCareGrade(),
                request.getDementiaType(),
                request.getComorbidities(),
                request.getContraindications(),
                request.getMedicationInfo(),
                request.getPrnMedicationInfo(),
                request.getPrimaryDoctorInfo(),
                request.getLikes(),
                request.getDislikes(),
                request.getSoothingWords(),
                request.getIneffectiveWords(),
                request.getSundowningInfo(),
                request.getRepetitiveBehaviors(),
                request.getWanderingRoute(),
                request.getEmergencyContacts(),
                request.getPreferredHospital(),
                request.getSpecialNotes() // 특이사항 매핑
        );
    }
}