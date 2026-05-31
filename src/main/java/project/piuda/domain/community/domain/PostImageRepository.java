package project.piuda.domain.community.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    void deleteAllByPost(Post post);
}
