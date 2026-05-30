package project.piuda.domain.community.presentation;

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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Long>> createComment(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentRequest request) {
        Long commentId = commentService.createComment(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("commentId", commentId));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CommentRequest request) {
        commentService.updateComment(commentId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/adopt")
    public ResponseEntity<Void> adoptComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.adoptComment(postId, commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}/adopt")
    public ResponseEntity<Void> cancelAdoption(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.cancelAdoption(postId, commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
