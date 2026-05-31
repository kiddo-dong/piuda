package project.piuda.domain.community.application;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.community.application.dto.CommentRequest;
import project.piuda.domain.community.application.dto.CommentResponse;
import project.piuda.domain.community.domain.Comment;
import project.piuda.domain.community.domain.CommentRepository;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createComment(Long postId, String userEmail, CommentRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        User writer = getUser(userEmail);

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
            if (!parentComment.getPost().getId().equals(postId)) {
                throw new BusinessException("해당 게시글의 댓글이 아닙니다.");
            }
            if (parentComment.getParentComment() != null) {
                throw new BusinessException("대댓글에는 답글을 달 수 없습니다.");
            }
        }

        Comment comment = Comment.builder()
                .post(post)
                .writer(writer)
                .parentComment(parentComment)
                .content(request.getContent())
                .build();
        return commentRepository.save(comment).getId();
    }

    public List<CommentResponse> getComments(Long postId) {
        return commentRepository.findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(postId).stream()
                .map(comment -> {
                    List<CommentResponse> replies = commentRepository
                            .findByParentCommentIdOrderByCreatedAtAsc(comment.getId()).stream()
                            .map(reply -> new CommentResponse(reply, List.of()))
                            .collect(Collectors.toList());
                    return new CommentResponse(comment, replies);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateComment(Long commentId, String userEmail, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
        User user = getUser(userEmail);

        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 댓글만 수정할 수 있습니다.");
        }
        comment.update(request.getContent());
    }

    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));
        User user = getUser(userEmail);

        if (!comment.getWriter().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 댓글만 삭제할 수 있습니다.");
        }
        commentRepository.delete(comment);
    }

    @Transactional
    @CacheEvict(value = "ranking", allEntries = true)
    public void adoptComment(Long postId, Long commentId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        User requester = getUser(userEmail);

        if (!post.getWriter().getId().equals(requester.getId())) {
            throw new ForbiddenException("게시글 작성자만 채택할 수 있습니다.");
        }
        if (post.isHasAdopted()) {
            throw new BusinessException("이미 채택된 게시글입니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.getPost().getId().equals(postId)) {
            throw new BusinessException("해당 게시글의 댓글이 아닙니다.");
        }
        if (comment.getParentComment() != null) {
            throw new BusinessException("대댓글은 채택할 수 없습니다.");
        }
        if (comment.getWriter().getId().equals(requester.getId())) {
            throw new BusinessException("본인의 댓글은 채택할 수 없습니다.");
        }

        comment.adopt();
        post.markAdopted();
        comment.getWriter().addScore(10);
    }

    @Transactional
    @CacheEvict(value = "ranking", allEntries = true)
    public void cancelAdoption(Long postId, Long commentId, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        User requester = getUser(userEmail);

        if (!post.getWriter().getId().equals(requester.getId())) {
            throw new ForbiddenException("게시글 작성자만 채택을 취소할 수 있습니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (!comment.isAdopted()) {
            throw new BusinessException("채택된 댓글이 아닙니다.");
        }

        comment.cancelAdoption();
        post.unmarkAdopted();
        comment.getWriter().subtractScore(10);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }
}
