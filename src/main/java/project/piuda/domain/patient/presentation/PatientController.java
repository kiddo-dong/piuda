package project.piuda.domain.patient.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.patient.application.PatientService;
import project.piuda.domain.patient.application.dto.PatientJoinRequest;
import project.piuda.domain.patient.application.dto.PatientRegisterRequest;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.global.security.CustomUserDetails;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientDetails(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        PatientResponse response = patientService.getPatientDetails(patientId, userDetails.getId());
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

    @PostMapping("/join")
    public ResponseEntity<PatientResponse> joinPatient(
            @RequestBody PatientJoinRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        PatientResponse response = patientService.joinPatient(request, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<PatientResponse>> getMyPatients(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(patientService.getMyPatients(userDetails.getId()));
    }
}