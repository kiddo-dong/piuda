package project.piuda.domain.user.application.dto;

import lombok.Getter;
import project.piuda.domain.user.domain.CaregiverProfile;
import project.piuda.domain.user.domain.Role;
import project.piuda.domain.user.domain.User;

import java.time.LocalDate;

@Getter
public class PublicUserResponse {
    private final String nickname;
    private final String profileImageUrl;
    private final String role;
    private final String introduction;
    private final Integer experienceYears;
    private final String caregiverType;
    private final int score;
    private final LocalDate joinedAt;

    public PublicUserResponse(User user, CaregiverProfile caregiverProfile) {
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.introduction = user.getIntroduction();
        this.experienceYears = (user.getRole() == Role.CAREGIVER && caregiverProfile != null)
                ? caregiverProfile.getExperienceYears() : null;
        this.caregiverType = (user.getRole() == Role.CAREGIVER && caregiverProfile != null && caregiverProfile.getCaregiverType() != null)
                ? caregiverProfile.getCaregiverType().name() : null;
        this.score = user.getScore();
        this.joinedAt = user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate() : null;
    }
}
