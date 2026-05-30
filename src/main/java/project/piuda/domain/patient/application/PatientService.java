package project.piuda.domain.patient.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceRepository;
import project.piuda.domain.patientmemory.domain.PatientMemory;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.patient.application.dto.PatientJoinRequest;
import project.piuda.domain.patient.application.dto.PatientRegisterRequest;
import project.piuda.domain.patient.application.dto.PatientResponse;

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

    /**
     * 환자에게 IoT 디바이스 기기를 연동하는 흐름
     */
    @Transactional
    public void connectDevice(Long patientId, String deviceSerial) {
        // 1. 애그리거트 루트 조회
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));

        Device device = deviceRepository.findByDeviceSerial(deviceSerial)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 디바이스 시리얼입니다."));

        // 2. 도메인 객체에게 비즈니스 로직 위임 (DDD 핵심)
        patient.assignDevice(device);

        // 3. 트랜잭션 종료 시 더티 체킹(Dirty Checking)으로 RDB 자동 업데이트
    }

    // 기존 PatientService 클래스 내부에 추가
    @Transactional
    public PatientResponse registerPatient(PatientRegisterRequest request, Long protectorId) {
        // 1. 로그인한 보호자 조회
        User protector = userRepository.findById(protectorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 환자(Aggregate Root) 엔티티 생성 및 저장
        Patient patient = Patient.builder()
                .name(request.getName())
                .birthDate(request.getBirthDate())
                .gender(request.getGender())
                .dementiaStage(request.getDementiaStage())
                .build();
        Patient savedPatient = patientRepository.save(patient);

        // 3. 환자 생성 시 1:1 관계인 빈 메모리 북도 마스터 데이터를 함께 생성해 줌
        PatientMemory patientMemory = PatientMemory.builder()
                .patient(savedPatient)
                .build();
        patientMemoryRepository.save(patientMemory);


        // 4. 환자-보호자 매핑 연관 데이터 생성 및 저장 (DDD: 도메인 간의 관계 맺기)
        PatientMember patientMember = PatientMember.builder()
                .patient(savedPatient)
                .user(protector)
                .build();
        patientMemberRepository.save(patientMember);

        return patientMapper.toResponseDto(savedPatient);
    }

    @Transactional
    public PatientResponse joinPatient(PatientJoinRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Patient patient = patientRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대코드입니다."));

        boolean alreadyJoined = patientMemberRepository.existsByPatientAndUser(patient, user);
        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 연결된 환자입니다.");
        }

        PatientMember patientMember = PatientMember.builder()
                .patient(patient)
                .user(user)
                .build();
        patientMemberRepository.save(patientMember);

        return patientMapper.toResponseDto(patient);
    }

    public List<PatientResponse> getMyPatients(Long userId) {
        return patientMemberRepository.findByUserId(userId).stream()
                .map(pm -> patientMapper.toResponseDto(pm.getPatient()))
                .toList();
    }

    public PatientResponse getPatientDetails(Long patientId, Long userId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new IllegalArgumentException("해당 환자에 대한 접근 권한이 없습니다.");
        }

        return patientMapper.toResponseDto(patient);
    }
}