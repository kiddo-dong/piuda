package project.piuda.domain.community.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.hidden = false " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "AND (:cursor IS NULL OR p.id < :cursor) " +
           "ORDER BY p.id DESC")
    List<Post> searchPostsLatest(@Param("category") PostCategory category,
                                 @Param("keyword") String keyword,
                                 @Param("cursor") Long cursor,
                                 org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.hidden = false " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "ORDER BY p.viewCount DESC, p.id DESC")
    List<Post> searchPostsByViews(@Param("category") PostCategory category,
                                  @Param("keyword") String keyword,
                                  org.springframework.data.domain.Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.hidden = false " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "ORDER BY p.likeCount DESC, p.id DESC")
    List<Post> searchPostsByLikes(@Param("category") PostCategory category,
                                  @Param("keyword") String keyword,
                                  org.springframework.data.domain.Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount + 1 WHERE p.id = :id")
    void incrementLikeCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Post p SET p.likeCount = p.likeCount - 1 WHERE p.id = :id AND p.likeCount > 0")
    void decrementLikeCount(@Param("id") Long id);
}
