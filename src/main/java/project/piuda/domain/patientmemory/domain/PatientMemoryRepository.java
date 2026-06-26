package project.piuda.domain.patientmemory.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientMemoryRepository extends JpaRepository<PatientMemory, Long> {
    Optional<PatientMemory> findByPatientId(Long patientId);

    void deleteByPatientId(Long patientId);
}
