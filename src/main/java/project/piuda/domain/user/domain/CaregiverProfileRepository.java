package project.piuda.domain.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CaregiverProfileRepository extends JpaRepository<CaregiverProfile, Long> {
    java.util.Optional<CaregiverProfile> findByUser(User user);
}