package project.piuda.domain.dailylog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.time.LocalDate;
import java.util.List;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    List<DailyLog> findByPatientIdOrderByLogDateDesc(Long patientId);

    List<DailyLog> findByPatientIdAndImageUrlIsNotNullOrderByLogDateDesc(Long patientId);

    List<DailyLog> findByPatientIdAndLogDateBetweenOrderByLogDateAsc(Long patientId, LocalDate from, LocalDate to);

    List<DailyLog> findAllByWriter(User writer);

    void deleteAllByWriter(User writer);

    void deleteAllByPatientId(Long patientId);
}