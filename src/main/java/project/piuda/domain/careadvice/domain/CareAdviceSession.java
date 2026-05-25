package project.piuda.domain.careadvice.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_advice_sessions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareAdviceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CareAdviceSession(User user, Patient patient) {
        this.user = user;
        this.patient = patient;
        this.createdAt = LocalDateTime.now();
    }
}
