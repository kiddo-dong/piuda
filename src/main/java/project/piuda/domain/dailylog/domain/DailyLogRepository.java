package project.piuda.domain.dailylog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    // 특정 환자의 모든 일지를 날짜 역순으로 조회하는 메서드 예시
    List<DailyLog> findByPatientIdOrderByLogDateDesc(Long patientId);

    // 특정 환자의 특정 날짜 일지가 이미 존재하는지 확인하는 메서드 예시
    boolean existsByPatientIdAndLogDate(Long patientId, LocalDate logDate);
}