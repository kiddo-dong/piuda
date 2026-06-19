package project.piuda.domain.user.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    boolean existsByNickname(String nickname);
    List<User> findAllByOrderByScoreDesc(Pageable pageable);
    Page<User> findAllByOrderByCreatedAtDesc(Pageable pageable);
    long countByRole(Role role);
}
