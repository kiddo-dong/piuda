package project.piuda.domain.dailylog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    // 특정 환자의 일지 전체 목록 조회 (날짜 역순 정렬)
    List<DailyLog> findByPatientIdOrderByLogDateDesc(Long patientId);
}