package project.piuda.domain.carejudgment.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogListResponse;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogRequest;
import project.piuda.domain.carejudgment.application.dto.CareJudgmentLogResponse;
import project.piuda.domain.carejudgment.domain.CareJudgmentLog;
import project.piuda.domain.carejudgment.domain.CareJudgmentLogRepository;
import project.piuda.domain.carejudgment.domain.JudgmentCategory;
import project.piuda.domain.carejudgment.domain.UrgencyLevel;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientMember;
import project.piuda.domain.patient.domain.PatientMemberRepository;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.Role;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.FcmService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CareJudgmentLogService {

    private final CareJudgmentLogRepository judgmentLogRepository;
    private final PatientRepository patientRepository;
    private final PatientMemberRepository patientMemberRepository;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @Transactional
    public Long createLog(Long patientId, String userEmail, CareJudgmentLogRequest request) {
        User writer = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, writer);

        // 작성은 간병인만
        if (writer.getRole() != Role.CAREGIVER) {
            throw new ForbiddenException("케어 판단 기록은 간병인만 작성할 수 있습니다.");
        }

        CareJudgmentLog log = judgmentLogRepository.save(CareJudgmentLog.builder()
                .patient(patient)
                .writer(writer)
                .category(request.getCategory())
                .situation(request.getSituation())
                .action(request.getAction())
                .rationale(request.getRationale())
                .urgency(request.getUrgency())
                .build());

        // 즉시공유면 보호자에게 FCM 푸시
        if (request.getUrgency() == UrgencyLevel.IMMEDIATE) {
            notifyProtectors(patient, writer, log);
        }

        return log.getId();
    }

    public CareJudgmentLogListResponse getLogs(Long patientId, JudgmentCategory category, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);

        List<CareJudgmentLog> logs = (category == null)
                ? judgmentLogRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                : judgmentLogRepository.findByPatientIdAndCategoryOrderByCreatedAtDesc(patientId, category);

        long immediateShareCount = judgmentLogRepository.countByPatientIdAndUrgency(patientId, UrgencyLevel.IMMEDIATE);

        return new CareJudgmentLogListResponse(
                immediateShareCount,
                logs.stream().map(CareJudgmentLogResponse::new).toList());
    }

    public CareJudgmentLogResponse getLog(Long patientId, Long logId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);
        return new CareJudgmentLogResponse(getOwnedLog(patientId, logId));
    }

    @Transactional
    public void updateLog(Long patientId, Long logId, String userEmail, CareJudgmentLogRequest request) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);

        CareJudgmentLog log = getOwnedLog(patientId, logId);
        validateWriter(log, user);
        log.update(request.getCategory(), request.getSituation(), request.getAction(),
                request.getRationale(), request.getUrgency());
    }

    @Transactional
    public void deleteLog(Long patientId, Long logId, String userEmail) {
        User user = getUser(userEmail);
        Patient patient = getPatient(patientId);
        validatePatientAccess(patient, user);

        CareJudgmentLog log = getOwnedLog(patientId, logId);
        validateWriter(log, user);
        judgmentLogRepository.delete(log);
    }

    // ─── helpers ────────────────────────────────────────────────

    private void notifyProtectors(Patient patient, User writer, CareJudgmentLog log) {
        String title = "[즉시공유] " + patient.getName() + " 케어 판단";
        String body = log.getSituation() + " → " + log.getAction();
        for (PatientMember member : patientMemberRepository.findByPatientId(patient.getId())) {
            User receiver = member.getUser();
            if (receiver.getRole() == Role.PROTECTOR && !receiver.getId().equals(writer.getId())) {
                fcmService.send(receiver.getFcmToken(), title, body);
            }
        }
    }

    private CareJudgmentLog getOwnedLog(Long patientId, Long logId) {
        CareJudgmentLog log = judgmentLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 판단 기록입니다."));
        if (!log.getPatient().getId().equals(patientId)) {
            throw new ForbiddenException("해당 환자의 판단 기록이 아닙니다.");
        }
        return log;
    }

    private void validateWriter(CareJudgmentLog log, User user) {
        if (!log.getWriter().getId().equals(user.getId())) {
            throw new ForbiddenException("본인이 작성한 기록만 수정·삭제할 수 있습니다.");
        }
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private Patient getPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 환자입니다."));
    }

    private void validatePatientAccess(Patient patient, User user) {
        if (!patientMemberRepository.existsByPatientAndUser(patient, user)) {
            throw new ForbiddenException("해당 환자에 대한 접근 권한이 없습니다.");
        }
    }
}
