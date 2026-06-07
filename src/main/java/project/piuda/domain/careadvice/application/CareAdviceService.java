package project.piuda.domain.careadvice.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.careadvice.application.dto.*;
import project.piuda.domain.careadvice.domain.*;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.patientmemory.domain.PatientMemoryRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.RagChatClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareAdviceService {

    private static final int CONTEXT_MESSAGE_LIMIT = 10;

    private final CareAdviceSessionRepository sessionRepository;
    private final CareAdviceMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final PatientMemoryRepository patientMemoryRepository;
    private final RagChatClient ragChatClient;

    @Transactional
    public CareAdviceSessionResponse createSession(Long patientId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));

        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }

        return new CareAdviceSessionResponse(sessionRepository.save(
                CareAdviceSession.builder().user(user).patient(patient).build()
        ));
    }

    @Transactional
    public SendCareAdviceResponse sendMessage(Long sessionId, String userEmail, CareAdviceMessageRequest request) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션에만 접근할 수 있습니다.");
        }

        List<CareAdviceMessage> recentHistory = messageRepository.findRecentMessages(
                sessionId, PageRequest.of(0, CONTEXT_MESSAGE_LIMIT)
        );
        Collections.reverse(recentHistory);

        CareAdviceMessage userMessage = messageRepository.save(
                CareAdviceMessage.builder()
                        .session(session)
                        .role(MessageRole.USER)
                        .content(request.getContent())
                        .build()
        );

        String patientContext = buildPatientContext(session.getPatient());
        String aiResponse = ragChatClient.sendMessage(recentHistory, request.getContent(), patientContext);

        CareAdviceMessage assistantMessage = messageRepository.save(
                CareAdviceMessage.builder()
                        .session(session)
                        .role(MessageRole.ASSISTANT)
                        .content(aiResponse)
                        .build()
        );

        return new SendCareAdviceResponse(
                new CareAdviceMessageResponse(userMessage),
                new CareAdviceMessageResponse(assistantMessage)
        );
    }

    @Transactional
    public void deleteSession(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션만 삭제할 수 있습니다.");
        }
        sessionRepository.delete(session);
    }

    public List<CareAdviceSessionResponse> getSessions(Long patientId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));

        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }

        return sessionRepository.findByUserIdAndPatientIdOrderByCreatedAtDesc(user.getId(), patientId)
                .stream()
                .map(CareAdviceSessionResponse::new)
                .collect(Collectors.toList());
    }

    public List<CareAdviceMessageResponse> getMessages(Long sessionId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CareAdviceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 세션입니다."));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인의 세션에만 접근할 수 있습니다.");
        }

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(CareAdviceMessageResponse::new)
                .collect(Collectors.toList());
    }

    private String buildPatientContext(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append("현재 돌보는 환자 정보:\n");
        sb.append("- 이름: ").append(patient.getName()).append("\n");
        if (patient.getBirthDate() != null) sb.append("- 생년월일: ").append(patient.getBirthDate()).append("\n");
        if (patient.getGender() != null) sb.append("- 성별: ").append(patient.getGender()).append("\n");
        if (patient.getDementiaStage() != null) sb.append("- 치매 단계: ").append(patient.getDementiaStage()).append("\n");

        patientMemoryRepository.findByPatientId(patient.getId()).ifPresent(memory -> {
            appendIfPresent(sb, "치매 유형", memory.getDementiaType());
            appendIfPresent(sb, "장기요양 등급", memory.getLongTermCareGrade() > 0 ? String.valueOf(memory.getLongTermCareGrade()) : null);
            appendIfPresent(sb, "혈액형", memory.getBloodType());
            appendIfPresent(sb, "동반 질환", memory.getComorbidities());
            appendIfPresent(sb, "복용 약물", memory.getMedicationInfo());
            appendIfPresent(sb, "금기 사항", memory.getContraindications());
            appendIfPresent(sb, "좋아하는 것", memory.getLikes());
            appendIfPresent(sb, "싫어하는 것", memory.getDislikes());
            appendIfPresent(sb, "진정 효과 있는 말", memory.getSoothingWords());
            appendIfPresent(sb, "역효과 나는 말", memory.getIneffectiveWords());
            appendIfPresent(sb, "석양증후군 정보", memory.getSundowningInfo());
            appendIfPresent(sb, "반복 행동", memory.getRepetitiveBehaviors());
            appendIfPresent(sb, "배회 경로", memory.getWanderingRoute());
            appendIfPresent(sb, "특이사항", memory.getSpecialNotes());
        });

        return sb.toString();
    }

    private void appendIfPresent(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append("- ").append(label).append(": ").append(value).append("\n");
        }
    }
}
