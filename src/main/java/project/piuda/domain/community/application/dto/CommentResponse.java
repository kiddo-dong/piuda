package project.piuda.domain.community.application.dto;

import lombok.Getter;
import project.piuda.domain.community.domain.Comment;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {
    private final Long commentId;
    private final String writerName;
    private final String writerRole;
    private final String content;
    private final boolean adopted;
    private final LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.writerRole = comment.getWriter().getRole().name();
        this.content = comment.getContent();
        this.adopted = comment.isAdopted();
        this.createdAt = comment.getCreatedAt();
    }
}
