package project.piuda.domain.aireport.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.domain.aireport.application.dto.AiReportResponse;

import java.util.List;

/**
 * AI 주간 돌봄 리포트.
 *
 * TODO: 데이터 소스 변경(DailyLog 제거)으로 내부 로직을 전면 재구축 예정.
 *       현재는 컨트롤러/응답 스키마 유지를 위한 골격만 남겨둔 상태이며, 모든 메소드는 미구현이다.
 */
@Service
@RequiredArgsConstructor
public class AiReportService {

    private static final String NOT_IMPLEMENTED = "AI 리포트 기능은 재구축 예정입니다.";

    public AiReportResponse generateReport(Long patientId, String userEmail) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    public List<AiReportResponse> getReports(Long patientId, String userEmail) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    public AiReportResponse getLatestReport(Long patientId, String userEmail) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    public AiReportResponse getReport(Long patientId, Long reportId, String userEmail) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }

    public void deleteReport(Long patientId, Long reportId, String userEmail) {
        throw new UnsupportedOperationException(NOT_IMPLEMENTED);
    }
}
