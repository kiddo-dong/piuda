package project.piuda.domain.community.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.application.dto.PostRequest;
import project.piuda.domain.community.application.dto.PostResponse;
import project.piuda.domain.community.domain.PostCategory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Long>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") PostRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        Long postId = postService.createPost(userDetails.getUsername(), request, image);
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) PostCategory category,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(postService.getPosts(userDetails.getUsername(), category, keyword));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.getPost(postId, userDetails.getUsername()));
    }

    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") PostRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException {
        postService.updatePost(postId, userDetails.getUsername(), request, image);
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
