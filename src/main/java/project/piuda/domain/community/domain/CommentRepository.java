package project.piuda.domain.community.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(Long postId);
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
    List<Comment> findByParentCommentIdInOrderByCreatedAtAsc(List<Long> parentCommentIds);

    List<Comment> findAllByWriter(User writer);

    void deleteAllByWriter(User writer);

    void deleteAllByPostIn(List<Post> posts);
}
