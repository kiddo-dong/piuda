package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final Long parentCommentId;
    private final String writerNickname;
    private final String writerProfileImageUrl;
    private final String writerRole;
    private final String content;
    private final boolean adopted;
    private final boolean hidden;
    private final LocalDateTime createdAt;
    private final List<CommentResponse> replies;

    public CommentResponse(Comment comment, List<CommentResponse> replies) {
        this.commentId = comment.getId();
        this.parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        this.writerNickname = comment.getWriter().getNickname();
        this.writerProfileImageUrl = comment.getWriter().getProfileImageUrl();
        this.writerRole = comment.getWriter().getRole() != null ? comment.getWriter().getRole().name() : null;
        this.hidden = comment.isHidden();
        this.content = comment.isHidden() ? "[신고로 인해 숨겨진 댓글입니다]" : comment.getContent();
        this.adopted = comment.isAdopted();
        this.createdAt = comment.getCreatedAt();
        this.replies = replies;
    }
}
