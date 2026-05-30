package project.piuda.domain.calendar.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareCalendarRepository extends JpaRepository<CareCalendar, Long> {
    List<CareCalendar> findByPatientIdOrderByStartTimeAsc(Long patientId);
    void deleteByDailyLogId(Long dailyLogId);
}