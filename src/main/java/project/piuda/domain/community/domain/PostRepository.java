package project.piuda.domain.community.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE (:category IS NULL OR p.category = :category) " +
           "AND (:keyword IS NULL OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) " +
           "AND (:cursor IS NULL OR p.id < :cursor) " +
           "ORDER BY p.id DESC")
    List<Post> searchPosts(@Param("category") PostCategory category,
                           @Param("keyword") String keyword,
                           @Param("cursor") Long cursor,
                           org.springframework.data.domain.Pageable pageable);
}
