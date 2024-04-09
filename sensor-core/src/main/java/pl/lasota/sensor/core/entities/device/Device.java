package pl.lasota.sensor.core.entities.device;

import jakarta.persistence.*;
import lombok.*;
import pl.lasota.sensor.core.entities.DeviceToken;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.sensor.Sensor;

import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "firmware_version", nullable = false)
    private String version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_token_id")
    private DeviceToken currentDeviceToken;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_config_id")
    private DeviceConfig currentDeviceConfig;

    @OneToMany(mappedBy = "device", fetch = FetchType.LAZY)
    private List<Sensor> sensor = new ArrayList<>();

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
