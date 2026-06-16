package project.piuda.domain.patientmemory.application;

import project.piuda.domain.patientmemory.application.dto.PatientMemoryRequest;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryResponse;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.global.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientMemoryService {

    private final PatientMemoryRepository patientMemoryRepository;

    public PatientMemoryResponse getPatientMemory(Long patientId) {
        PatientMemory patientMemory = patientMemoryRepository.findByPatientId(patientId)
                .orElseThrow(() -> new NotFoundException("해당 환자의 정보가 존재하지 않습니다."));
        return new PatientMemoryResponse(patientMemory);
    }

    @Transactional
    public void updatePatientMemory(Long patientId, PatientMemoryRequest request) {
        PatientMemory patientMemory = patientMemoryRepository.findByPatientId(patientId)
                .orElseThrow(() -> new NotFoundException("해당 환자의 정보가 존재하지 않습니다."));

        patientMemory.updateContent(
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
                request.getSpecialNotes()
        );
    }
}
