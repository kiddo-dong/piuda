package project.piuda.domain.memorybook.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoryBookRepository extends JpaRepository<MemoryBook, Long> {
    Optional<MemoryBook> findByPatientId(Long PatientId);
}