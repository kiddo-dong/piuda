package project.piuda.user.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.piuda.user.domain.User;
import project.piuda.user.domain.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void signup(String email, String password) {

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new RuntimeException("이미 존재하는 이메일");
                });

        User user = new User(email, password);
        userRepository.save(user);
    }

    public User login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("비밀번호 틀림");
        }

        return user;
    }
}