package project.piuda.domain.calendar.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface CareCalendarRepository extends JpaRepository<CareCalendar, Long> {
    List<CareCalendar> findByPatientIdOrderByStartTimeAsc(Long patientId);
    void deleteByDailyLogId(Long dailyLogId);

    void deleteAllByWriter(User writer);

    @Modifying
    @Query("UPDATE CareCalendar c SET c.assignee = null WHERE c.assignee = :user")
    void clearAssignee(@Param("user") User user);
}