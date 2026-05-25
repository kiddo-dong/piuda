package project.piuda.domain.community.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.application.dto.PostRequest;
import project.piuda.domain.community.application.dto.PostResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Map<String, Long>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostRequest request) {
        Long postId = postService.createPost(userDetails.getUsername(), request);
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPosts(userDetails.getUsername()));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPost(postId, userDetails.getUsername()));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PostRequest request) {
        postService.updatePost(postId, userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = postService.toggleLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }
}
