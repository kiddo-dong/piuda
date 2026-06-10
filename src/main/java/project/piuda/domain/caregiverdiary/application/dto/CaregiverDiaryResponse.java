package project.piuda.domain.caregiverdiary.application.dto;

import lombok.Getter;
import project.piuda.domain.caregiverdiary.domain.CaregiverDiary;
import project.piuda.domain.caregiverdiary.domain.MoodType;

import java.time.LocalDateTime;

@Getter
public class CaregiverDiaryResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final MoodType mood;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CaregiverDiaryResponse(CaregiverDiary diary) {
        this.id = diary.getId();
        this.title = diary.getTitle();
        this.content = diary.getContent();
        this.mood = diary.getMood();
        this.createdAt = diary.getCreatedAt();
        this.updatedAt = diary.getUpdatedAt();
    }
}
