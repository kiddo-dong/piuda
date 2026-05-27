package project.piuda.domain.community.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.community.application.dto.PostRequest;
import project.piuda.domain.community.application.dto.PostResponse;
import project.piuda.domain.community.domain.*;
import project.piuda.domain.community.domain.PostCategory;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(String userEmail, PostRequest request) {
        User writer = getUser(userEmail);
        Post post = Post.builder()
                .writer(writer)
                .title(request.getTitle())
                .content(request.getContent())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .build();
        return postRepository.save(post).getId();
    }

    public List<PostResponse> getPosts(String userEmail, PostCategory category) {
        User user = getUser(userEmail);
        List<Post> posts = (category == null)
                ? postRepository.findAllByOrderByCreatedAtDesc()
                : postRepository.findAllByCategoryOrderByCreatedAtDesc(category);
        return posts.stream()
                .map(post -> new PostResponse(post, postLikeRepository.existsByPostIdAndUserId(post.getId(), user.getId())))
                .collect(Collectors.toList());
    }

    public PostResponse getPost(Long postId, String userEmail) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        return new PostResponse(post, postLikeRepository.existsByPostIdAndUserId(postId, user.getId()));
    }

    @Transactional
    public void updatePost(Long postId, String userEmail, PostRequest request) {
        User user = getUser(userEmail);
        Post post = getPostOrThrow(postId);
        validateOwner(post.getWriter().getId(), user.getId());
        post.update(request.getTitle(), request.getContent(), request.getCategory(), request.getImageUrl());
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

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    private Post getPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    private void validateOwner(Long writerId, Long userId) {
        if (!writerId.equals(userId)) {
            throw new IllegalArgumentException("본인의 게시글만 수정/삭제할 수 있습니다.");
        }
    }
}
