package project.piuda.domain.careadvice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "care_advice_messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CareAdviceMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private CareAdviceSession session;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageRole role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public CareAdviceMessage(CareAdviceSession session, MessageRole role, String content) {
        this.session = session;
        this.role = role;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}
