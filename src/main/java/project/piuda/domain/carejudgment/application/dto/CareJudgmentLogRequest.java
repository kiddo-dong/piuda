package project.piuda.domain.carejudgment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import project.piuda.domain.carejudgment.domain.JudgmentCategory;
import project.piuda.domain.carejudgment.domain.UrgencyLevel;

@Getter
public class CareJudgmentLogRequest {

    @NotNull(message = "상황 카테고리를 선택해주세요.")
    private JudgmentCategory category;

    @NotBlank(message = "어떤 상황이었는지 입력해주세요.")
    @Size(max = 500, message = "상황은 500자 이하로 입력해주세요.")
    private String situation;

    @NotBlank(message = "어떻게 했는지 입력해주세요.")
    @Size(max = 500, message = "판단 내용은 500자 이하로 입력해주세요.")
    private String action;

    @NotBlank(message = "그렇게 한 이유를 입력해주세요.")
    @Size(max = 500, message = "근거는 500자 이하로 입력해주세요.")
    private String rationale;

    @NotNull(message = "공유 긴급도를 선택해주세요.")
    private UrgencyLevel urgency;
}
