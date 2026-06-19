package project.piuda.domain.caregiverdiary.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface CaregiverDiaryRepository extends JpaRepository<CaregiverDiary, Long> {
    List<CaregiverDiary> findByUserIdOrderByCreatedAtDesc(Long userId);

    void deleteAllByUser(User user);
}
