package project.piuda.domain.user.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.user.application.dto.LoginRequest;
import project.piuda.domain.user.application.dto.SignUpRequest;
import project.piuda.domain.user.application.dto.TokenResponse;
import project.piuda.domain.user.domain.*;
import project.piuda.global.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CaregiverProfileRepository caregiverProfileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // 암호화

    @Transactional
    public void signUp(SignUpRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        userRepository.save(user);

        // 간병인일 경우 서브 프로필 추가 생성
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
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtTokenProvider.createToken(user.getId(), user.getEmail(), user.getRole().name());
        return new TokenResponse(token);
    }
}