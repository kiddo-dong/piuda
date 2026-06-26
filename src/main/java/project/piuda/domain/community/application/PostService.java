package project.piuda.domain.community.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import project.piuda.domain.community.application.dto.*;
import project.piuda.domain.community.domain.*;
import project.piuda.domain.report.domain.ReportRepository;
import project.piuda.domain.report.domain.ReportTargetType;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private static final int MAX_IMAGE_COUNT = 8;

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Long createPost(String userEmail, PostRequest request, List<MultipartFile> images) throws IOException {
        User writer = getUser(userEmail);
        validateImageCount(images);

        Post post = Post.builder()
                .writer(writer)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .build();
        postRepository.save(post);

        uploadAndSaveImages(images, post);
        return post.getId();
    }

    public PostPageResponse getPosts(String userEmail, PostCategory category, String keyword,
                                     Long cursor, int page, int size, SortType sortType) {
        List<Post> posts;
        if (sortType == SortType.VIEWS) {
            posts = postRepository.searchPostsByViews(category, keyword, PageRequest.of(page, size + 1));
        } else if (sortType == SortType.LIKES) {
            posts = postRepository.searchPostsByLikes(category, keyword, PageRequest.of(page, size + 1));
        } else {
            posts = postRepository.searchPostsLatest(category, keyword, cursor, PageRequest.of(0, size + 1));
        }

        boolean hasNext = posts.size() > size;
        List<Post> content = hasNext ? posts.subList(0, size) : posts;

        List<Long> postIds = content.stream().map(Post::getId).collect(Collectors.toList());
        Set<Long> likedPostIds;
        Set<Long> scrappedPostIds;
        if (userEmail == null || postIds.isEmpty()) {
            likedPostIds = Set.of();
            scrappedPostIds = Set.of();
        } else {
            Long userId = getUser(userEmail).getId();
            likedPostIds = postLikeRepository.findLikedPostIds(postIds, userId);
            scrappedPostIds = postScrapRepository.findScrappedPostIds(postIds, userId);
        }

        List<PostResponse> responses = content.stream()
                .map(post -> new PostResponse(post, likedPostIds.contains(post.getId()), scrappedPostIds.contains(post.getId())))
                .collect(Collectors.toList());

        if (sortType == SortType.LATEST) {
            return new PostPageResponse(responses, hasNext);
        }
        return new PostPageResponse(responses, hasNext, page);
    }

    @Transactional
    public PostResponse getPost(Long postId, String userEmail) {
        postRepository.incrementViewCount(postId);
        Post post = getPostOrThrow(postId);
        if (post.isHidden()) {
            throw new BusinessException("신고로 인해 숨겨진 게시글입니다.");
        }
        boolean likedByMe = false;
        boolean scrappedByMe = false;
        if (userEmail != null) {
            Long userId = getUser(userEmail).getId();
            likedByMe = postLikeRepository.existsByPostIdAndUserId(postId, userId);
            scrappedByMe = postScrapRepository.existsByPostIdAndUserId(postId, userId);
        }
        return new PostResponse(post, likedByMe, scrappedByMe);
    }

    @Transactional
    public void updatePost(Long postId, String userEmail, PostRequest request, List<MultipartFile> images) throws IOException {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        validateOwner(post.getWriter().getId(), user.getId());
        if (post.isHidden()) {
            throw new BusinessException("신고로 인해 숨겨진 게시글은 수정할 수 없습니다.");
        }
        validateImageCount(images);

        post.update(request.getTitle(), request.getContent(), request.getCategory());

        if (images != null && !images.isEmpty()) {
            postImageRepository.deleteAllByPost(post);
            uploadAndSaveImages(images, post);
        }
    }

    @Transactional
    public void deletePost(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        validateOwner(post.getWriter().getId(), user.getId());
        deletePostCascade(post);
    }

    /**
     * 소유자 검증 없이 게시글과 모든 하위 데이터를 삭제한다.
     * 신고 누적에 의한 자동 삭제(ReportService) 등 시스템 삭제 경로에서 사용.
     */
    @Transactional
    public void forceDeletePost(Post post) {
        deletePostCascade(post);
    }

    private void deletePostCascade(Post post) {
        List<Post> single = List.of(post);

        // 댓글에 대한 신고 정리 (대댓글 포함)
        List<Long> commentIds = commentRepository.findByPostId(post.getId()).stream()
                .map(Comment::getId).collect(Collectors.toList());
        if (!commentIds.isEmpty()) {
            reportRepository.deleteAllByTargetTypeAndTargetIdIn(ReportTargetType.COMMENT, commentIds);
        }
        // 게시글 자체에 대한 신고 정리
        reportRepository.deleteAllByTargetTypeAndTargetId(ReportTargetType.POST, post.getId());

        // 댓글 · 좋아요 · 스크랩 삭제 (이미지는 JPA cascade)
        commentRepository.deleteAllByPostIn(single);
        postLikeRepository.deleteAllByPostIn(single);
        postScrapRepository.deleteAllByPostIn(single);

        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleScrap(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        if (post.isHidden()) {
            throw new BusinessException("숨겨진 게시글은 스크랩할 수 없습니다.");
        }

        return postScrapRepository.findByPostIdAndUserId(postId, user.getId())
                .map(scrap -> {
                    postScrapRepository.delete(scrap);
                    return false;
                })
                .orElseGet(() -> {
                    postScrapRepository.save(PostScrap.builder().post(post).user(user).build());
                    return true;
                });
    }

    @Transactional
    public void deleteScrap(Long postId, String userEmail) {
        User user = getUser(userEmail);
        PostScrap scrap = postScrapRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new NotFoundException("스크랩하지 않은 게시글입니다."));
        postScrapRepository.delete(scrap);
    }

    public ScrappedPostPageResponse getScrappedPosts(String userEmail, PostCategory category, SortType sortType, int page, int size) {
        User user = getUser(userEmail);
        PageRequest pageRequest = PageRequest.of(page, size + 1);
        List<PostScrap> scraps;
        if (sortType == SortType.VIEWS) {
            scraps = postScrapRepository.findScrappedPostsByUserIdOrderByViews(user.getId(), category, pageRequest);
        } else if (sortType == SortType.LIKES) {
            scraps = postScrapRepository.findScrappedPostsByUserIdOrderByLikes(user.getId(), category, pageRequest);
        } else {
            scraps = postScrapRepository.findScrappedPostsByUserIdOrderByLatest(user.getId(), category, pageRequest);
        }

        boolean hasNext = scraps.size() > size;
        List<PostScrap> content = hasNext ? scraps.subList(0, size) : scraps;

        List<Long> postIds = content.stream().map(s -> s.getPost().getId()).collect(Collectors.toList());
        Set<Long> likedPostIds = postIds.isEmpty() ? Set.of() : postLikeRepository.findLikedPostIds(postIds, user.getId());

        List<ScrappedPostResponse> responses = content.stream()
                .map(scrap -> new ScrappedPostResponse(scrap.getPost(), likedPostIds.contains(scrap.getPost().getId()), scrap.getScrappedAt()))
                .collect(Collectors.toList());

        return new ScrappedPostPageResponse(responses, hasNext, page);
    }

    @Transactional
    public boolean toggleLike(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);

        return postLikeRepository.findByPostIdAndUserId(postId, user.getId())
                .map(like -> {
                    postLikeRepository.delete(like);
                    postRepository.decrementLikeCount(postId);
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().post(post).user(user).build());
                    postRepository.incrementLikeCount(postId);
                    return true;
                });
    }

    private void uploadAndSaveImages(List<MultipartFile> images, Post post) throws IOException {
        if (images == null || images.isEmpty()) return;
        for (MultipartFile image : images) {
            if (image != null && !image.isEmpty()) {
                String imageUrl = s3UploadService.upload(image, "posts");
                postImageRepository.save(PostImage.builder().post(post).imageUrl(imageUrl).build());
            }
        }
    }

    private void validateImageCount(List<MultipartFile> images) {
        if (images != null && images.size() > MAX_IMAGE_COUNT) {
            throw new BusinessException("이미지는 최대 " + MAX_IMAGE_COUNT + "장까지 업로드할 수 있습니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
    }

    private void validateOwner(Long writerId, Long userId) {
        if (!writerId.equals(userId)) {
            throw new ForbiddenException("본인의 게시글만 수정/삭제할 수 있습니다.");
        }
    }
}
