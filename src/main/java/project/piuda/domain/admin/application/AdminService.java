package project.piuda.domain.admin.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.piuda.domain.admin.application.dto.*;
import project.piuda.domain.community.domain.Post;
import project.piuda.domain.community.domain.PostRepository;
import project.piuda.domain.device.domain.Device;
import project.piuda.domain.device.domain.DeviceRepository;
import project.piuda.domain.patient.domain.Patient;
import project.piuda.domain.patient.domain.PatientRepository;
import project.piuda.domain.user.domain.Role;
import project.piuda.domain.user.domain.User;
import project.piuda.domain.user.domain.UserRepository;
import project.piuda.global.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final DeviceRepository deviceRepository;
    private final PatientRepository patientRepository;

    public Page<AdminUserResponse> getUsers(int page, int size) {
        return userRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size))
                .map(AdminUserResponse::new);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 사용자입니다."));
        userRepository.delete(user);
    }

    public Page<AdminPostResponse> getPosts(int page, int size) {
        return postRepository.findAll(PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by("createdAt").descending()))
                .map(AdminPostResponse::new);
    }

    @Transactional
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 게시글입니다."));
        postRepository.delete(post);
    }

    public List<AdminDeviceResponse> getDevices() {
        return deviceRepository.findAll().stream()
                .map(device -> {
                    String patientName = patientRepository.findByDeviceDeviceSerial(device.getDeviceSerial())
                            .map(Patient::getName)
                            .orElse(null);
                    return new AdminDeviceResponse(device, patientName);
                })
                .toList();
    }

    @Transactional
    public void deleteDevice(Long deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new NotFoundException("존재하지 않는 기기입니다."));
        patientRepository.findByDeviceDeviceSerial(device.getDeviceSerial())
                .ifPresent(Patient::removeDevice);
        deviceRepository.delete(device);
    }

    public AdminStatsResponse getStats() {
        return new AdminStatsResponse(
                userRepository.count(),
                userRepository.countByRole(Role.PROTECTOR),
                userRepository.countByRole(Role.CAREGIVER),
                userRepository.countByRole(Role.MEDICAL_STAFF),
                postRepository.count(),
                deviceRepository.count()
        );
    }
}
