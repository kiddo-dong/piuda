package project.piuda.device.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(unique = true)
    private String deviceId; // ESP32 chipId (Hard Ware)

    @Column(nullable = false)
    private String deviceKeyHash;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status = DeviceStatus.ACTIVE;

    public Device(String deviceId, String deviceKeyHash) {
        this.deviceId = deviceId;
        this.deviceKeyHash = deviceKeyHash;
        this.status = DeviceStatus.ACTIVE;
    }

    public boolean isActive() {
        return status == DeviceStatus.ACTIVE;
    }
}
