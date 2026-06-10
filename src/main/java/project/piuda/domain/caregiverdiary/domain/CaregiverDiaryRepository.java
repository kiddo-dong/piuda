package project.piuda.domain.caregiverdiary.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaregiverDiaryRepository extends JpaRepository<CaregiverDiary, Long> {
    List<CaregiverDiary> findByUserIdOrderByCreatedAtDesc(Long userId);
}
