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
    private final String writerRole;
    private final String content;
    private final boolean adopted;
    private final LocalDateTime createdAt;
    private final List<CommentResponse> replies;

    public CommentResponse(Comment comment, List<CommentResponse> replies) {
        this.commentId = comment.getId();
        this.parentCommentId = comment.getParentComment() != null ? comment.getParentComment().getId() : null;
        this.writerNickname = comment.getWriter().getNickname();
        this.writerRole = comment.getWriter().getRole() != null ? comment.getWriter().getRole().name() : null;
        this.content = comment.getContent();
        this.adopted = comment.isAdopted();
        this.createdAt = comment.getCreatedAt();
        this.replies = replies;
    }
}
