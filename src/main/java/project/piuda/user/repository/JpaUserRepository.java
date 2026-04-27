package project.piuda.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.user.domain.User;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
