package project.piuda.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.user.application.dto.LoginRequest;
import project.piuda.domain.user.application.dto.RankingResponse;
import project.piuda.domain.user.application.dto.SignUpRequest;
import project.piuda.domain.user.application.dto.TokenResponse;
import project.piuda.domain.user.application.dto.UserResponse;
import project.piuda.domain.user.application.dto.UserUpdateRequest;
import project.piuda.domain.user.domain.*;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ConflictException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.security.JwtTokenProvider;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        if (request.getRole() == Role.CAREGIVER) {
            CaregiverProfile profile = CaregiverProfile.builder()
                    .user(user)
                    .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                    .introduction(request.getIntroduction())
                    .build();
            caregiverProfileRepository.save(profile);
        }
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());
        return new TokenResponse(token);
    }

    public UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        return new UserResponse(user);
    }

    @Transactional
    public void updateMe(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        String encodedPassword = request.getPassword() != null && !request.getPassword().isBlank()
                ? passwordEncoder.encode(request.getPassword()) : null;
        user.update(request.getName(), request.getPhone(), encodedPassword);
    }

    @Transactional
    public void deleteMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    @Cacheable(value = "ranking", key = "#limit")
    public List<RankingResponse> getRanking(int limit) {
        List<User> users = userRepository.findAllByOrderByScoreDesc(PageRequest.of(0, limit));
        List<RankingResponse> result = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            result.add(new RankingResponse(i + 1, users.get(i)));
        }
        return result;
    }
}
