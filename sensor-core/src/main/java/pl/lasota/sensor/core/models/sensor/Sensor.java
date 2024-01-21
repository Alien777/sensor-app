package pl.lasota.sensor.core.models.sensor;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import pl.lasota.sensor.core.models.device.Device;
import pl.lasota.sensor.core.models.MessageType;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sensor_seq")
    @SequenceGenerator(name = "sensor_seq", sequenceName = "sensor_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    @Column(name = "time")
    private OffsetDateTime time;

}
