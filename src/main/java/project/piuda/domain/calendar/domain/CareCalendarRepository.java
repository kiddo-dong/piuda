package project.piuda.domain.calendar.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CareCalendarRepository extends JpaRepository<CareCalendar, Long> {
    // 특정 환자의 모든 공유 캘린더 일정을 시작 시간 순으로 조회
    List<CareCalendar> findByPatientIdOrderByStartTimeAsc(Long patientId);
}