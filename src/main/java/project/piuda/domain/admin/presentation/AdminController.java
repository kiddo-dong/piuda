package project.piuda.domain.admin.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.admin.application.AdminService;
import project.piuda.domain.admin.application.dto.*;

import java.util.List;

@Tag(name = "Admin", description = "관리자 전용 API — ADMIN 권한 필요")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "전체 통계 조회", description = "회원 수, 게시글 수, 기기 수 등 기본 통계를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @Operation(summary = "전체 회원 목록 조회", description = "가입일 최신순으로 페이징 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserResponse>> getUsers(
            @Parameter(description = "페이지 번호 (0부터)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getUsers(page, size));
    }

    @Operation(summary = "회원 강제 탈퇴", description = "특정 회원을 강제 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 회원 ID") @PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 게시글 목록 조회", description = "최신순으로 페이징 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/posts")
    public ResponseEntity<Page<AdminPostResponse>> getPosts(
            @Parameter(description = "페이지 번호 (0부터)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.getPosts(page, size));
    }

    @Operation(summary = "게시글 강제 삭제", description = "특정 게시글을 강제 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "삭제할 게시글 ID") @PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 기기 목록 조회", description = "등록된 모든 IoT 기기와 연동 환자 정보를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/devices")
    public ResponseEntity<List<AdminDeviceResponse>> getDevices() {
        return ResponseEntity.ok(adminService.getDevices());
    }

    @Operation(summary = "기기 강제 삭제", description = "특정 기기를 삭제합니다. 연동된 환자가 있으면 자동 해제됩니다.")
    @ApiResponse(responseCode = "200", description = "삭제 성공")
    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "삭제할 기기 ID") @PathVariable Long deviceId) {
        adminService.deleteDevice(deviceId);
        return ResponseEntity.ok().build();
    }
}
