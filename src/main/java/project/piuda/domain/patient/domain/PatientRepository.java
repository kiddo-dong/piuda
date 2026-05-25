package project.piuda.domain.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    // ESP32 디바이스 시리얼로 연동된 환자 조회
    Optional<Patient> findByDeviceDeviceSerial(String deviceSerial);
}