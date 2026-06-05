package project.piuda.domain.community.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    Optional<PostScrap> findByPostIdAndUserId(Long postId, Long userId);
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Query("SELECT ps.post.id FROM PostScrap ps WHERE ps.post.id IN :postIds AND ps.user.id = :userId")
    Set<Long> findScrappedPostIds(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);

    @Query("SELECT ps.post FROM PostScrap ps WHERE ps.user.id = :userId AND (:category IS NULL OR ps.post.category = :category) ORDER BY ps.id DESC")
    List<Post> findScrappedPostsByUserIdOrderByLatest(@Param("userId") Long userId, @Param("category") PostCategory category, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT ps.post FROM PostScrap ps WHERE ps.user.id = :userId AND (:category IS NULL OR ps.post.category = :category) ORDER BY ps.post.viewCount DESC, ps.post.id DESC")
    List<Post> findScrappedPostsByUserIdOrderByViews(@Param("userId") Long userId, @Param("category") PostCategory category, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT ps.post FROM PostScrap ps WHERE ps.user.id = :userId AND (:category IS NULL OR ps.post.category = :category) ORDER BY ps.post.likeCount DESC, ps.post.id DESC")
    List<Post> findScrappedPostsByUserIdOrderByLikes(@Param("userId") Long userId, @Param("category") PostCategory category, org.springframework.data.domain.Pageable pageable);
}
