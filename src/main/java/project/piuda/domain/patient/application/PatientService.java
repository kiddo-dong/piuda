package project.piuda.domain.patient.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceRepository;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.patient.application.dto.PatientJoinRequest;
import project.piuda.domain.patient.application.dto.PatientCreateRequest;
import project.piuda.domain.patient.application.dto.PatientUpdateRequest;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.global.exception.ConflictException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;

import java.util.List;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMember;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.patient.presentation.PatientMapper;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final PatientMapper patientMapper;
    private final PatientMemoryRepository patientMemoryRepository;

    @Transactional
    public void disconnectDevice(Long patientId, Long userId) {
        Patient patient = getPatient(patientId);
        User user = getUserById(userId);
        validatePatientAccess(patient, user);
        patient.removeDevice();
    }

    @Transactional
    public void connectDevice(Long patientId, Long userId, String deviceSerial) {
        Patient patient = getPatient(patientId);
        User user = getUserById(userId);
        validatePatientAccess(patient, user);
        Device device = deviceRepository.findByDeviceSerial(deviceSerial)
                .orElseThrow(() -> new NotFoundException("등록되지 않은 디바이스 시리얼입니다."));
        patient.assignDevice(device);
    }

    @Transactional
    public PatientResponse registerPatient(PatientCreateRequest request, Long protectorId) {
        User protector = getUserById(protectorId);

        Patient patient = Patient.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .dementiaStage(request.getDementiaStage())
                .build();
        Patient savedPatient = patientRepository.save(patient);

        patientMemoryRepository.save(PatientMemory.builder().patient(savedPatient).build());

        patientMemberRepository.save(PatientMember.builder()
                .patient(savedPatient)
                .user(protector)
                .relationship(request.getRelationship())
                .build());

        return patientMapper.toResponseDto(savedPatient);
    }

    @Transactional
    public PatientResponse joinPatient(PatientJoinRequest request, Long userId) {
        User user = getUserById(userId);

        Patient patient = patientRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new NotFoundException("유효하지 않은 초대코드입니다."));

        if (patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ConflictException("이미 연결된 환자입니다.");
        }

        patientMemberRepository.save(PatientMember.builder()
                .patient(patient)
                .user(user)
                .relationship(request.getRelationship())
                .build());
        return patientMapper.toResponseDto(patient);
    }

    public List<PatientResponse> getMyPatients(Long userId) {
        return patientMemberRepository.findByUserId(userId).stream()
                .map(pm -> patientMapper.toResponseDto(pm.getPatient()))
                .toList();
    }

    @Transactional
    public PatientResponse updatePatient(Long patientId, Long userId, PatientUpdateRequest request) {
        Patient patient = getPatient(patientId);
        User user = getUserById(userId);
        validatePatientAccess(patient, user);
        patient.update(request.getName(), request.getBirthDate(), request.getGender(), request.getDementiaStage());
        return patientMapper.toResponseDto(patient);
    }

    @Transactional
    public void deletePatient(Long patientId, Long userId) {
        Patient patient = getPatient(patientId);
        User user = getUserById(userId);
        validatePatientAccess(patient, user);
        patientRepository.delete(patient);
    }

    public PatientResponse getPatientDetails(Long patientId, Long userId) {
        Patient patient = getPatient(patientId);
        User user = getUserById(userId);
        validatePatientAccess(patient, user);
        return patientMapper.toResponseDto(patient);
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
