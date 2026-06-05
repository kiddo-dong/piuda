package project.piuda.domain.community.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.community.application.CommentService;
import project.piuda.domain.community.application.dto.CommentRequest;
import project.piuda.domain.community.application.dto.CommentResponse;

import java.util.List;
import java.util.Map;

@Tag(name = "Comment", description = "댓글 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성", description = "게시글에 댓글 또는 대댓글을 작성합니다. parentCommentId 포함 시 대댓글로 처리됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공 - commentId 반환"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Long>> createComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentRequest request) {
        Long commentId = commentService.createComment(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("commentId", commentId));
    }

    @Operation(summary = "댓글 목록 조회", description = "게시글의 댓글과 대댓글을 조회합니다. 비로그인도 조회 가능합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @Parameter(description = "게시글 ID") @PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @Operation(summary = "댓글 수정", description = "본인이 작성한 댓글만 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentRequest request) {
        commentService.updateComment(commentId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글만 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 채택", description = "게시글 작성자가 댓글을 채택합니다. 게시글당 1개만 채택 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채택 성공"),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 없음")
    })
    @PostMapping("/posts/{postId}/comments/{commentId}/adopt")
    public ResponseEntity<Void> adoptComment(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.adoptComment(postId, commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "댓글 채택 취소", description = "게시글 작성자가 채택된 댓글의 채택을 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "채택 취소 성공"),
            @ApiResponse(responseCode = "403", description = "게시글 작성자가 아님"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 없음")
    })
    @DeleteMapping("/posts/{postId}/comments/{commentId}/adopt")
    public ResponseEntity<Void> cancelAdoption(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @Parameter(description = "댓글 ID") @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.cancelAdoption(postId, commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
