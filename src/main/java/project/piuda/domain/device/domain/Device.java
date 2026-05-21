package project.piuda.domain.device.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String deviceSerial;

    @Enumerated(EnumType.STRING)
    private DeviceStatus deviceStatus;

    private LocalDateTime purchasedAt;
    private LocalDateTime lastConnectedAt;

    @Builder
    public Device(String deviceSerial) {
        this.deviceSerial = deviceSerial;
        this.deviceStatus = DeviceStatus.INACTIVE;
        this.purchasedAt = LocalDateTime.now();
    }

    public void updateStatus(DeviceStatus status) {
        this.deviceStatus = status;
        this.lastConnectedAt = LocalDateTime.now();
    }
}