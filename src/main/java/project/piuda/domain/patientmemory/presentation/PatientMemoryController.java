package project.piuda.domain.patientmemory.presentation;

import project.piuda.domain.patientmemory.application.PatientMemoryService;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryRequest;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/patients/{patientId}/patient-memory")
@RequiredArgsConstructor
public class PatientMemoryController {

    private final PatientMemoryService patientMemoryService;

    @GetMapping
    public ResponseEntity<PatientMemoryResponse> getPatientMemory(@PathVariable Long patientId) {
        return ResponseEntity.ok(patientMemoryService.getPatientMemory(patientId));
    }

    @PutMapping
    public ResponseEntity<Void> updatePatientMemory(
            @PathVariable Long patientId,
            @RequestBody PatientMemoryRequest request) {

        patientMemoryService.updatePatientMemory(patientId, request);
        return ResponseEntity.ok().build();
    }
}
