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

    private String deviceKey;

    public Device(String deviceId, String deviceKey) {
        this.deviceId = deviceId;
        this.deviceKey = deviceKey;
    }
}