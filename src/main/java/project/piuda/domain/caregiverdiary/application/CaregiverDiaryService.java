package project.piuda.domain.caregiverdiary.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.caregiverdiary.application.dto.CaregiverDiaryRequest;
import project.piuda.domain.caregiverdiary.application.dto.CaregiverDiaryResponse;
import project.piuda.domain.caregiverdiary.domain.CaregiverDiary;
import project.piuda.domain.caregiverdiary.domain.CaregiverDiaryRepository;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CaregiverDiaryService {

    private final CaregiverDiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createDiary(String userEmail, CaregiverDiaryRequest request) {
        User user = getUser(userEmail);
        CaregiverDiary diary = diaryRepository.save(CaregiverDiary.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .mood(request.getMood())
                .build());
        return diary.getId();
    }

    public List<CaregiverDiaryResponse> getDiaries(String userEmail) {
        User user = getUser(userEmail);
        return diaryRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(CaregiverDiaryResponse::new)
                .collect(Collectors.toList());
    }

    public CaregiverDiaryResponse getDiary(Long diaryId, String userEmail) {
        User user = getUser(userEmail);
        CaregiverDiary diary = getDiaryById(diaryId);
        validateOwner(diary, user);
        return new CaregiverDiaryResponse(diary);
    }

    @Transactional
    public void updateDiary(Long diaryId, String userEmail, CaregiverDiaryRequest request) {
        User user = getUser(userEmail);
        CaregiverDiary diary = getDiaryById(diaryId);
        validateOwner(diary, user);
        diary.update(request.getTitle(), request.getContent(), request.getMood());
    }

    @Transactional
    public void deleteDiary(Long diaryId, String userEmail) {
        User user = getUser(userEmail);
        CaregiverDiary diary = getDiaryById(diaryId);
        validateOwner(diary, user);
        diaryRepository.delete(diary);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
    }

    private CaregiverDiary getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 일기입니다."));
    }

    private void validateOwner(CaregiverDiary diary, User user) {
        if (!diary.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인이 작성한 일기만 접근할 수 있습니다.");
        }
    }
}
