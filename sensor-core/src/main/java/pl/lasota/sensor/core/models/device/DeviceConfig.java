package pl.lasota.sensor.core.models.device;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "config", columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private String config;

    @Column(name = "time")
    private OffsetDateTime time;

    @Column(name = "for_version", nullable = false)
    private String forVersion;

}
