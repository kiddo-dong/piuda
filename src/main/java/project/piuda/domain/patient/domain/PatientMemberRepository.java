package project.piuda.domain.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.patient.domain.PatientMember;
import project.piuda.domain.patient.domain.PatientMemberId;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface PatientMemberRepository extends JpaRepository<PatientMember, PatientMemberId> {

    boolean existsByPatientAndUser(Patient patient, User user);

    List<PatientMember> findByUserId(Long userId);
}