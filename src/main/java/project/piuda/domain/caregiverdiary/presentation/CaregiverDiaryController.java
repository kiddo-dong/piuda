package project.piuda.domain.caregiverdiary.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.caregiverdiary.application.CaregiverDiaryService;
import project.piuda.domain.caregiverdiary.application.dto.CaregiverDiaryRequest;
import project.piuda.domain.caregiverdiary.application.dto.CaregiverDiaryResponse;

import java.util.List;

@Tag(name = "CaregiverDiary", description = "간병일기 API")
@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class CaregiverDiaryController {

    private final CaregiverDiaryService diaryService;

    @Operation(summary = "일기 작성")
    @ApiResponse(responseCode = "200", description = "작성 성공 - 일기 ID 반환")
    @PostMapping
    public ResponseEntity<Long> createDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CaregiverDiaryRequest request) {
        return ResponseEntity.ok(diaryService.createDiary(userDetails.getUsername(), request));
    }

    @Operation(summary = "내 일기 목록 조회", description = "본인이 작성한 일기 목록을 최신순으로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<List<CaregiverDiaryResponse>> getDiaries(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(diaryService.getDiaries(userDetails.getUsername()));
    }

    @Operation(summary = "일기 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일기 없음")
    })
    @GetMapping("/{diaryId}")
    public ResponseEntity<CaregiverDiaryResponse> getDiary(
            @Parameter(description = "일기 ID") @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(diaryService.getDiary(diaryId, userDetails.getUsername()));
    }

    @Operation(summary = "일기 수정", description = "본인이 작성한 일기만 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일기 없음")
    })
    @PutMapping("/{diaryId}")
    public ResponseEntity<Void> updateDiary(
            @Parameter(description = "일기 ID") @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CaregiverDiaryRequest request) {
        diaryService.updateDiary(diaryId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일기 삭제", description = "본인이 작성한 일기만 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "일기 없음")
    })
    @DeleteMapping("/{diaryId}")
    public ResponseEntity<Void> deleteDiary(
            @Parameter(description = "일기 ID") @PathVariable Long diaryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        diaryService.deleteDiary(diaryId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
