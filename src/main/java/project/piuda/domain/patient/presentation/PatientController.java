package project.piuda.domain.patient.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.patient.application.PatientService;
import project.piuda.domain.patient.application.dto.PatientRegisterRequest;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientRepository patientRepository; // 단순 조환은 서비스 안 거치고 바로 조회하기도 함(CQRS 가벼운 적용)
    private final PatientMapper patientMapper;

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientDetails(@PathVariable Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다."));

        // MapStruct를 이용한 DTO 변환
        PatientResponse response = patientMapper.toResponseDto(patient);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{patientId}/devices")
    public ResponseEntity<Void> connectDevice(
            @PathVariable Long patientId,
            @RequestParam String deviceSerial) {

        patientService.connectDevice(patientId, deviceSerial);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<PatientResponse> registerPatient(
            @RequestBody PatientRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) { // Security컨텍스트에서 유저 정보 추출

        PatientResponse response = patientService.registerPatient(request, userDetails.getId());
        return ResponseEntity.ok(response);
    }
}