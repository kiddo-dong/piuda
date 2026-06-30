package project.piuda.domain.carejudgment.application.dto;

import lombok.Getter;

import java.util.List;

/**
 * 보호자 보관소 조회 응답 — 기록 목록 + 즉시공유(IMMEDIATE) 건수.
 * (목업의 "🔔 즉시공유 N건" 표기에 사용. 즉시공유 건수는 카테고리 필터와 무관하게 환자 전체 기준)
 */
@Getter
public class CareJudgmentLogListResponse {

    private final long immediateShareCount;
    private final List<CareJudgmentLogResponse> logs;

    public CareJudgmentLogListResponse(long immediateShareCount, List<CareJudgmentLogResponse> logs) {
        this.immediateShareCount = immediateShareCount;
        this.logs = logs;
    }
}
