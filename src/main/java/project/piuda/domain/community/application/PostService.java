package project.piuda.domain.community.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.community.application.dto.PostPageResponse;
import project.piuda.domain.community.application.dto.PostRequest;
import project.piuda.domain.community.application.dto.PostResponse;
import project.piuda.domain.community.domain.*;
import project.piuda.domain.community.domain.SortType;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.data.domain.PageRequest;
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
        User user = getUser(userEmail);

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
        Set<Long> likedPostIds = postIds.isEmpty()
                ? Set.of()
                : postLikeRepository.findLikedPostIds(postIds, user.getId());

        List<PostResponse> responses = content.stream()
                .map(post -> new PostResponse(post, likedPostIds.contains(post.getId())))
                .collect(Collectors.toList());

        if (sortType == SortType.LATEST) {
            return new PostPageResponse(responses, hasNext);
        }
        return new PostPageResponse(responses, hasNext, page);
    }

    @Transactional
    public PostResponse getPost(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        post.increaseViewCount();
        return new PostResponse(post, postLikeRepository.existsByPostIdAndUserId(postId, user.getId()));
    }

    @Transactional
    public void updatePost(Long postId, String userEmail, PostRequest request, List<MultipartFile> images) throws IOException {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        validateOwner(post.getWriter().getId(), user.getId());
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
        postRepository.delete(post);
    }

    @Transactional
    public boolean toggleLike(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);

        return postLikeRepository.findByPostIdAndUserId(postId, user.getId())
                .map(like -> {
                    postLikeRepository.delete(like);
                    post.decreaseLike();
                    return false;
                })
                .orElseGet(() -> {
                    postLikeRepository.save(PostLike.builder().post(post).user(user).build());
                    post.increaseLike();
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
