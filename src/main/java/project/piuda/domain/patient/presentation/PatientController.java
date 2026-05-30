package project.piuda.domain.patient.presentation;

import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<PatientResponse> registerPatient(
            @Valid @RequestBody PatientRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.registerPatient(request, userDetails.getId()));
    }

    @PostMapping("/join")
    public ResponseEntity<PatientResponse> joinPatient(
            @Valid @RequestBody PatientJoinRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.joinPatient(request, userDetails.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<PatientResponse>> getMyPatients(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getMyPatients(userDetails.getId()));
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientDetails(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getPatientDetails(patientId, userDetails.getId()));
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatientRegisterRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(patientId, userDetails.getId(), request));
    }

    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.deletePatient(patientId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{patientId}/devices")
    public ResponseEntity<Void> connectDevice(
            @PathVariable Long patientId,
            @RequestParam String deviceSerial) {
        patientService.connectDevice(patientId, deviceSerial);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{patientId}/devices")
    public ResponseEntity<Void> disconnectDevice(
            @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.disconnectDevice(patientId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
