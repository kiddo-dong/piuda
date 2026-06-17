package project.piuda.domain.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.report.application.ReportService;
import project.piuda.domain.report.application.dto.ReportRequest;

@Tag(name = "Report", description = "신고 API")
@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "게시글 신고",
            description = "게시글을 신고합니다. 사유: SPAM / OBSCENE / ABUSE / MISINFORMATION / COPYRIGHT / OTHER")
    @ApiResponse(responseCode = "200", description = "신고 접수 완료")
    @PostMapping("/api/v1/posts/{postId}/reports")
    public ResponseEntity<Void> reportPost(
            @Parameter(description = "신고할 게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReportRequest request) {
        reportService.reportPost(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 신고",
            description = "댓글을 신고합니다. 사유: SPAM / OBSCENE / ABUSE / MISINFORMATION / COPYRIGHT / OTHER")
    @ApiResponse(responseCode = "200", description = "신고 접수 완료")
    @PostMapping("/api/v1/comments/{commentId}/reports")
    public ResponseEntity<Void> reportComment(
            @Parameter(description = "신고할 댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReportRequest request) {
        reportService.reportComment(commentId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}
