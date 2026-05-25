package project.piuda.domain.dailylog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByPatientIdOrderByLogDateDesc(Long patientId);

    // 이미지가 첨부된 일지만 조회 (갤러리용)
    List<DailyLog> findByPatientIdAndImageUrlIsNotNullOrderByLogDateDesc(Long patientId);
}