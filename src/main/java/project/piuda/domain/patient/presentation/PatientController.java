package project.piuda.domain.patient.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.patient.application.PatientService;
import project.piuda.domain.patient.application.dto.PatientJoinRequest;
import project.piuda.domain.patient.application.dto.PatientCreateRequest;
import project.piuda.domain.patient.application.dto.PatientUpdateRequest;
import project.piuda.domain.patient.application.dto.PatientResponse;
import project.piuda.global.security.CustomUserDetails;

@Tag(name = "Patient", description = "환자 관리 API")
@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Operation(summary = "환자 등록", description = "새 환자를 등록합니다. 등록 시 PatientMemory 빈 레코드가 자동 생성됩니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @PostMapping
    public ResponseEntity<PatientResponse> registerPatient(
            @Valid @RequestBody PatientCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.registerPatient(request, userDetails.getId()));
    }

    @Operation(summary = "초대코드로 환자 합류", description = "초대코드를 통해 기존 환자 케어 그룹에 합류합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "합류 성공"),
            @ApiResponse(responseCode = "404", description = "유효하지 않은 초대코드")
    })
    @PostMapping("/join")
    public ResponseEntity<PatientResponse> joinPatient(
            @Valid @RequestBody PatientJoinRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.joinPatient(request, userDetails.getId()));
    }

    @Operation(summary = "내 환자 목록 조회", description = "로그인한 사용자가 케어하는 환자 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/my")
    public ResponseEntity<List<PatientResponse>> getMyPatients(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getMyPatients(userDetails.getId()));
    }

    @Operation(summary = "환자 상세 조회", description = "환자 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientResponse> getPatientDetails(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(patientService.getPatientDetails(patientId, userDetails.getId()));
    }

    @Operation(summary = "환자 정보 수정", description = "환자 기본 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @PutMapping("/{patientId}")
    public ResponseEntity<PatientResponse> updatePatient(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PatientUpdateRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(patientId, userDetails.getId(), request));
    }

    @Operation(summary = "환자 삭제", description = "환자 정보를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @DeleteMapping("/{patientId}")
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.deletePatient(patientId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "디바이스 연동", description = "환자에게 IoT 디바이스를 연결합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연동 성공"),
            @ApiResponse(responseCode = "404", description = "환자 또는 디바이스 없음")
    })
    @PostMapping("/{patientId}/devices")
    public ResponseEntity<Void> connectDevice(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @Parameter(description = "디바이스 시리얼 번호") @RequestParam String deviceSerial,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.connectDevice(patientId, userDetails.getId(), deviceSerial);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "디바이스 연동 해제", description = "환자에게 연결된 IoT 디바이스를 해제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연동 해제 성공"),
            @ApiResponse(responseCode = "404", description = "환자 없음")
    })
    @DeleteMapping("/{patientId}/devices")
    public ResponseEntity<Void> disconnectDevice(
            @Parameter(description = "환자 ID") @PathVariable Long patientId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        patientService.disconnectDevice(patientId, userDetails.getId());
        return ResponseEntity.ok().build();
    }
}
