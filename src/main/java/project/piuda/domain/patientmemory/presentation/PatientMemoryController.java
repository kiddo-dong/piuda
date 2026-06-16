package project.piuda.domain.patientmemory.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.piuda.domain.patientmemory.application.PatientMemoryService;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryRequest;
import project.piuda.domain.patientmemory.application.dto.PatientMemoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PatientMemory", description = "환자 신상/의료 정보 API")
@RestController
@RequestMapping("/api/v1/patients/{patientId}/patient-memory")
@RequiredArgsConstructor
public class PatientMemoryController {

    private final PatientMemoryService patientMemoryService;

    @Operation(summary = "환자 신상/의료 정보 조회", description = "환자 1인당 1개의 신상 및 의료 정보 레코드를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @GetMapping
    public ResponseEntity<PatientMemoryResponse> getPatientMemory(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(patientMemoryService.getPatientMemory(patientId, userDetails.getUsername()));
    }

    @Operation(summary = "환자 신상/의료 정보 수정", description = "환자의 신상 및 의료 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PutMapping
    public ResponseEntity<Void> updatePatientMemory(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PatientMemoryRequest request) {
        patientMemoryService.updatePatientMemory(patientId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}
