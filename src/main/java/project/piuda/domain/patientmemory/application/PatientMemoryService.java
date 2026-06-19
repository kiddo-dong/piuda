package project.piuda.domain.patientmemory.application;

import project.piuda.domain.patientmemory.application.dto.PatientMemoryRequest;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryResponse;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientMemoryService {

    private final PatientMemoryRepository patientMemoryRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;

    public PatientMemoryResponse getPatientMemory(Long patientId, String userEmail) {
        validateAccess(patientId, userEmail);
        return new PatientMemoryResponse(findPatientMemory(patientId));
    }

    @Transactional
    public void updatePatientMemory(Long patientId, String userEmail, PatientMemoryRequest request) {
        validateAccess(patientId, userEmail);
        PatientMemory patientMemory = findPatientMemory(patientId);

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

    private PatientMemory findPatientMemory(Long patientId) {
        return patientMemoryRepository.findByPatientId(patientId)
                .orElseThrow(() -> new NotFoundException("해당 환자의 정보가 존재하지 않습니다."));
    }

    private void validateAccess(Long patientId, String userEmail) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
