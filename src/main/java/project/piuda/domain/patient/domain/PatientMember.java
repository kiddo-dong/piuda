package project.piuda.domain.patient.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "patient_members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatientMember {

    @EmbeddedId
    private PatientMemberId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("patientId")
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 20)
    private String relationship;

    private LocalDateTime joinedAt;

    @Builder
    public PatientMember(Patient patient, User user, String relationship) {
        this.id = new PatientMemberId(patient.getId(), user.getId());
        this.patient = patient;
        this.user = user;
        this.relationship = relationship;
        this.joinedAt = LocalDateTime.now();
    }
}