package project.piuda.domain.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.patient.domain.PatientMember;
import project.piuda.domain.patient.domain.PatientMemberId;

public interface PatientMemberRepository extends JpaRepository<PatientMember, PatientMemberId> {
}