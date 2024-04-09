package pl.lasota.sensor.core.entities.device;

import jakarta.persistence.*;
import lombok.*;
import pl.lasota.sensor.core.entities.DeviceToken;
import pl.lasota.sensor.core.entities.Member;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_device_token_id")
    private DeviceToken currentDeviceToken;

    @Column(name = "time")
    private OffsetDateTime time;
}
