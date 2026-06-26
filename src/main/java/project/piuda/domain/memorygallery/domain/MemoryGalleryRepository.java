package project.piuda.domain.memorygallery.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import project.piuda.domain.user.domain.User;

import java.util.List;

public interface MemoryGalleryRepository extends JpaRepository<MemoryGallery, Long> {
    List<MemoryGallery> findAllByPatientIdOrderByUploadedAtDesc(Long patientId);

    void deleteAllByWriter(User writer);

    void deleteAllByPatientId(Long patientId);
}
