package pl.lasota.sensor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTemporary {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_temporary_seq")
    @SequenceGenerator(name = "device_temporary_seq", sequenceName = "device_temporary_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "member_id")
    private String member;

    @Column(name = "device_id")
    private String device;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_token")
    private DeviceToken currentDeviceToken;

    @Column(name = "time")
    private OffsetDateTime time;
}
