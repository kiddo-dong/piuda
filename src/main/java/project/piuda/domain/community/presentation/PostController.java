package project.piuda.domain.community.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.application.dto.PostPageResponse;
import project.piuda.domain.community.application.dto.PostRequest;
import project.piuda.domain.community.application.dto.PostResponse;
import project.piuda.domain.community.domain.PostCategory;
import project.piuda.domain.community.domain.SortType;

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
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Long postId = postService.createPost(userDetails.getUsername(), request, images);
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    @GetMapping
    public ResponseEntity<PostPageResponse> getPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) PostCategory category,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPosts(email, category, keyword, cursor, page, size, sortType));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPost(postId, email));
    }

    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") PostRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        postService.updatePost(postId, userDetails.getUsername(), request, images);
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

    @PostMapping("/{postId}/scraps")
    public ResponseEntity<Map<String, Boolean>> toggleScrap(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean scrapped = postService.toggleScrap(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("scrapped", scrapped));
    }

    @GetMapping("/scraps")
    public ResponseEntity<PostPageResponse> getScrappedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getScrappedPosts(userDetails.getUsername(), page, size));
    }
}
