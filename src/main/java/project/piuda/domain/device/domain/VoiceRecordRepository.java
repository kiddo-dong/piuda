package project.piuda.domain.device.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoiceRecordRepository extends JpaRepository<VoiceRecord, Long> {
    List<VoiceRecord> findAllByPatientIdOrderByRecordedAtDesc(Long patientId);
}
