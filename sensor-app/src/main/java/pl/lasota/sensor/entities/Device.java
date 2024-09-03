package pl.lasota.sensor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "device")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @Column(name = "id", unique = true, updatable = false, length = 12)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "firmware_version")
    private String version;

    @Column(name = "member_id")
    private String member;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_token_id")
    private DeviceToken currentDeviceToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_config_id")
    private DeviceConfig currentDeviceConfig;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
