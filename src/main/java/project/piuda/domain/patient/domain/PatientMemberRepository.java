package project.piuda.domain.patient.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.piuda.domain.patient.domain.PatientMember;
import project.piuda.domain.patient.domain.PatientMemberId;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface PatientMemberRepository extends JpaRepository<PatientMember, PatientMemberId> {

    boolean existsByPatientAndUser(Patient patient, User user);

    List<PatientMember> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM PatientMember pm WHERE pm.user = :user")
    void deleteAllByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM PatientMember pm WHERE pm.patient = :patient")
    void deleteAllByPatient(@Param("patient") Patient patient);
}