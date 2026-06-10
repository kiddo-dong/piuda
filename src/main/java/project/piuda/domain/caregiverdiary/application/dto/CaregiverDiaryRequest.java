package project.piuda.domain.caregiverdiary.application.dto;

import lombok.Getter;
import project.piuda.domain.caregiverdiary.domain.MoodType;

@Getter
public class CaregiverDiaryRequest {
    private String title;
    private String content;
    private MoodType mood;
}
