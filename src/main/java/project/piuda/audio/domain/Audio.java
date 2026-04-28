package project.piuda.audio.domain;

import jakarta.persistence.*;
import lombok.*;
import project.piuda.device.domain.Device;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Audio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filePath;

    @ManyToOne
    private Device device;
}