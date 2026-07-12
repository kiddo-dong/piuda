package project.piuda.domain.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.admin.application.dto.*;
import project.piuda.domain.community.application.PostService;
import project.piuda.domain.community.domain.Comment;
import project.piuda.domain.community.domain.CommentRepository;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostRepository;
import project.piuda.domain.user.application.UserService;
import project.piuda.domain.report.application.dto.AdminReportResponse;
import project.piuda.domain.report.domain.Report;
import project.piuda.domain.report.domain.ReportRepository;
import project.piuda.domain.report.domain.ReportStatus;
import project.piuda.domain.report.domain.ReportTargetType;
import project.piuda.domain.user.domain.Role;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final PostService postService;

    public Page<AdminUserResponse> getUsers(int page, int size) {
        return userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(AdminUserResponse::new);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        // 회원 탈퇴와 동일하게 연관 데이터(14개 테이블)를 정리 후 삭제
        userService.deleteMe(user.getEmail());
    }

    public Page<AdminPostResponse> getPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending()))
                .map(AdminPostResponse::new);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        // 댓글·좋아요·스크랩·신고까지 정리 후 삭제 (FK 제약 위반 방지)
        postService.forceDeletePost(post);
    }

    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.PROTECTOR),
                userRepository.countByRole(Role.CAREGIVER),
                userRepository.countByRole(Role.MEDICAL_STAFF),
                postRepository.count()
        );
    }

    public Page<AdminReportResponse> getReports(int page, int size) {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING, PageRequest.of(page, size))
                .map(AdminReportResponse::new);
    }

    @Transactional
    public void dismissReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 신고입니다."));

        if (report.getTargetType() == ReportTargetType.POST) {
            postRepository.findById(report.getTargetId()).ifPresent(Post::unhide);
        } else if (report.getTargetType() == ReportTargetType.COMMENT) {
            commentRepository.findById(report.getTargetId()).ifPresent(Comment::unhide);
        }

        report.dismiss();
    }
}
