package project.piuda.domain.caregiverdiary.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import project.piuda.domain.caregiverdiary.domain.MoodType;

@Getter
public class CaregiverDiaryRequest {

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 100, message = "제목은 100자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotNull(message = "기분을 선택해주세요.")
    private MoodType mood;
}
