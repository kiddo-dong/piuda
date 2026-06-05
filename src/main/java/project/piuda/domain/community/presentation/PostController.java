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
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.application.dto.*;
import project.piuda.domain.community.domain.PostCategory;
import project.piuda.domain.community.domain.SortType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다. 이미지는 최대 8장까지 첨부 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작성 성공 - postId 반환"),
            @ApiResponse(responseCode = "400", description = "이미지 8장 초과")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Long>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") PostRequest request,
            @Parameter(description = "첨부 이미지 (최대 8장)") @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        Long postId = postService.createPost(userDetails.getUsername(), request, images);
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    @Operation(summary = "게시글 목록 조회", description = "카테고리, 키워드, 정렬 조합으로 게시글을 조회합니다. 비로그인도 가능합니다. LATEST는 커서 페이징, VIEWS/LIKES는 오프셋 페이징입니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<PostPageResponse> getPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "카테고리 필터 (생략 시 전체)") @RequestParam(required = false) PostCategory category,
            @Parameter(description = "제목/내용 키워드 검색") @RequestParam(required = false) String keyword,
            @Parameter(description = "커서 ID (LATEST 정렬 시 사용)") @RequestParam(required = false) Long cursor,
            @Parameter(description = "페이지 번호 (VIEWS/LIKES 정렬 시 사용, 0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 (LATEST: 최신순, VIEWS: 조회수순, LIKES: 좋아요순)") @RequestParam(defaultValue = "LATEST") SortType sortType) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPosts(email, category, keyword, cursor, page, size, sortType));
    }

    @Operation(summary = "게시글 단건 조회", description = "게시글 상세를 조회합니다. 조회 시 조회수가 1 증가합니다. 비로그인도 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(postService.getPost(postId, email));
    }

    @Operation(summary = "게시글 수정", description = "본인이 작성한 게시글만 수정할 수 있습니다. 이미지 전송 시 기존 이미지 전체 교체, 미전송 시 유지됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    public ResponseEntity<Void> updatePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("data") PostRequest request,
            @Parameter(description = "새 첨부 이미지 (최대 8장, 선택)") @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        postService.updatePost(postId, userDetails.getUsername(), request, images);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "게시글 삭제", description = "본인이 작성한 게시글만 삭제할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좋아요 토글", description = "게시글 좋아요를 토글합니다. 좋아요 상태이면 취소, 아니면 추가.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공 - liked: true(좋아요), false(취소)"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Map<String, Boolean>> toggleLike(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean liked = postService.toggleLike(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("liked", liked));
    }

    @Operation(summary = "게시글 스크랩 토글", description = "게시글을 스크랩하거나 스크랩을 해제합니다. 스크랩 상태이면 해제, 아니면 스크랩 추가.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공 - scrapped: true(스크랩), false(해제)"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/{postId}/scraps")
    public ResponseEntity<Map<String, Boolean>> toggleScrap(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean scrapped = postService.toggleScrap(postId, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("scrapped", scrapped));
    }

    @Operation(summary = "게시글 스크랩 취소", description = "스크랩한 게시글을 스크랩 목록에서 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩 취소 성공"),
            @ApiResponse(responseCode = "404", description = "게시글 없음 또는 스크랩하지 않은 게시글")
    })
    @DeleteMapping("/{postId}/scraps")
    public ResponseEntity<Void> deleteScrap(
            @Parameter(description = "게시글 ID") @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deleteScrap(postId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "내 스크랩 목록 조회", description = "로그인한 사용자의 스크랩 게시글 목록을 조회합니다. 카테고리 필터와 정렬(LATEST/VIEWS/LIKES)을 지원합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/scraps")
    public ResponseEntity<ScrappedPostPageResponse> getScrappedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "카테고리 필터 (생략 시 전체)") @RequestParam(required = false) PostCategory category,
            @Parameter(description = "정렬 기준 (LATEST: 스크랩 최신순, VIEWS: 조회수순, LIKES: 좋아요순)") @RequestParam(defaultValue = "LATEST") SortType sortType,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.getScrappedPosts(userDetails.getUsername(), category, sortType, page, size));
    }
}
