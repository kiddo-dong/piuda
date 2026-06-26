package project.piuda.domain.report.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.community.application.CommentService;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.domain.Comment;
import project.piuda.domain.community.domain.CommentRepository;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostRepository;
import project.piuda.domain.report.application.dto.ReportRequest;
import project.piuda.domain.report.domain.*;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ConflictException;
import project.piuda.global.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostService postService;
    private final CommentService commentService;

    @Value("${report.auto-hide-threshold:5}")
    private int autoHideThreshold;

    @Value("${report.auto-delete-threshold:10}")
    private int autoDeleteThreshold;

    @Transactional
    public void reportPost(Long postId, String userEmail, ReportRequest request) {
        User reporter = getUser(userEmail);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));

        if (post.getWriter().getId().equals(reporter.getId())) {
            throw new BusinessException("본인의 게시글은 신고할 수 없습니다.");
        }
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporter.getId(), ReportTargetType.POST, postId)) {
            throw new ConflictException("이미 신고한 게시글입니다.");
        }

        reportRepository.save(Report.builder()
                .reporter(reporter)
                .targetType(ReportTargetType.POST)
                .targetId(postId)
                .reason(request.getReason())
                .build());

        processPostReport(post);
    }

    @Transactional
    public void reportComment(Long commentId, String userEmail, ReportRequest request) {
        User reporter = getUser(userEmail);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 댓글입니다."));

        if (comment.getWriter().getId().equals(reporter.getId())) {
            throw new BusinessException("본인의 댓글은 신고할 수 없습니다.");
        }
        if (reportRepository.existsByReporterIdAndTargetTypeAndTargetId(reporter.getId(), ReportTargetType.COMMENT, commentId)) {
            throw new ConflictException("이미 신고한 댓글입니다.");
        }

        reportRepository.save(Report.builder()
                .reporter(reporter)
                .targetType(ReportTargetType.COMMENT)
                .targetId(commentId)
                .reason(request.getReason())
                .build());

        processCommentReport(comment);
    }

    private void processPostReport(Post post) {
        long count = reportRepository.countByTargetTypeAndTargetId(ReportTargetType.POST, post.getId());
        if (count >= autoDeleteThreshold) {
            // 신고·댓글·좋아요·스크랩까지 정리 후 삭제 (FK 제약 위반 방지)
            postService.forceDeletePost(post);
        } else if (count >= autoHideThreshold) {
            post.hide();
        }
    }

    private void processCommentReport(Comment comment) {
        long count = reportRepository.countByTargetTypeAndTargetId(ReportTargetType.COMMENT, comment.getId());
        if (count >= autoDeleteThreshold) {
            commentService.forceDeleteComment(comment);
        } else if (count >= autoHideThreshold) {
            comment.hide();
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }
}
