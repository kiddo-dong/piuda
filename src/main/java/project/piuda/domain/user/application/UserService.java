package project.piuda.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.user.application.dto.LoginRequest;
import project.piuda.domain.user.application.dto.OnboardingRequest;
import project.piuda.domain.user.application.dto.RankingResponse;
import project.piuda.domain.user.application.dto.SignUpRequest;
import project.piuda.domain.user.application.dto.TokenResponse;
import project.piuda.domain.user.application.dto.UserResponse;
import project.piuda.domain.user.application.dto.UserUpdateRequest;
import project.piuda.domain.user.domain.*;
import project.piuda.global.exception.BusinessException;
import project.piuda.global.exception.ConflictException;
import project.piuda.global.exception.ForbiddenException;
import project.piuda.global.exception.NotFoundException;
import project.piuda.global.infrastructure.S3UploadService;
import project.piuda.global.security.JwtTokenProvider;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final S3UploadService s3UploadService;

    @Transactional
    public void signUp(SignUpRequest request, MultipartFile image) throws IOException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ConflictException("이미 존재하는 이메일입니다.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }

        String profileImageUrl = (image != null && !image.isEmpty())
                ? s3UploadService.upload(image, "profiles") : null;

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .profileImageUrl(profileImageUrl)
                .introduction(request.getIntroduction())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        if (request.getRole() == Role.CAREGIVER) {
            CaregiverProfile profile = CaregiverProfile.builder()
                    .user(user)
                    .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                    .gender(request.getGender())
                    .birthDate(request.getBirthDate())
                    .build();
            caregiverProfileRepository.save(profile);
        }
    }

    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    @Transactional
    public TokenResponse completeOnboarding(String email, OnboardingRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
        user.completeOnboarding(request.getNickname(), request.getRole(), request.getPhone());
        if (request.getRole() == Role.CAREGIVER) {
            CaregiverProfile profile = CaregiverProfile.builder()
                    .user(user)
                    .experienceYears(request.getExperienceYears() != null ? request.getExperienceYears() : 0)
                    .gender(request.getGender())
                    .birthDate(request.getBirthDate())
                    .build();
            caregiverProfileRepository.save(profile);
        }
        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = issueRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (user.getPassword() == null) {
            throw new BusinessException("소셜 로그인 계정입니다. 소셜 로그인을 이용해 주세요.");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = issueRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new ForbiddenException("유효하지 않은 리프레시 토큰입니다."));

        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken);
            throw new ForbiddenException("리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요.");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshTokenValue = jwtTokenProvider.createRefreshToken();
        refreshToken.rotate(newRefreshTokenValue, jwtTokenProvider.getRefreshTokenExpiry());

        return new TokenResponse(newAccessToken, newRefreshTokenValue);
    }

    @Transactional
    public void logout(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        refreshTokenRepository.deleteByUser(user);
    }

    private String issueRefreshToken(User user) {
        String tokenValue = jwtTokenProvider.createRefreshToken();
        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                rt -> rt.rotate(tokenValue, jwtTokenProvider.getRefreshTokenExpiry()),
                () -> refreshTokenRepository.save(RefreshToken.builder()
                        .user(user)
                        .token(tokenValue)
                        .expiryDate(jwtTokenProvider.getRefreshTokenExpiry())
                        .build())
        );
        return tokenValue;
    }

    public UserResponse getMe(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        CaregiverProfile profile = user.getRole() == Role.CAREGIVER
                ? caregiverProfileRepository.findById(user.getId()).orElse(null) : null;
        return new UserResponse(user, profile);
    }

    @Transactional
    public void updateMe(String email, UserUpdateRequest request, MultipartFile image) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        if (request.getNickname() != null && !request.getNickname().isBlank()
                && !request.getNickname().equals(user.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다.");
        }
        String profileImageUrl = (image != null && !image.isEmpty())
                ? s3UploadService.upload(image, "profiles") : user.getProfileImageUrl();
        String encodedPassword = null;
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (user.getPassword() == null) {
                throw new BusinessException("소셜 로그인 계정은 비밀번호를 변경할 수 없습니다.");
            }
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
                throw new BusinessException("현재 비밀번호를 입력해주세요.");
            }
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException("현재 비밀번호가 일치하지 않습니다.");
            }
            encodedPassword = passwordEncoder.encode(request.getPassword());
        }
        user.update(request.getName(), request.getNickname(), request.getPhone(),
                profileImageUrl, request.getIntroduction(), encodedPassword);

        if (user.getRole() == Role.CAREGIVER) {
            CaregiverProfile profile = caregiverProfileRepository.findById(user.getId())
                    .orElseThrow(() -> new NotFoundException("간병인 프로필을 찾을 수 없습니다."));
            profile.update(request.getGender(), request.getBirthDate(), request.getExperienceYears());
        }
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
