package project.piuda.domain.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Optional<Patient> findByDeviceDeviceSerial(String deviceSerial);

    Optional<Patient> findByInviteCode(String inviteCode);
}