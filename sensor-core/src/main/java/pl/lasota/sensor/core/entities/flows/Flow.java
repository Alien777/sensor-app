package pl.lasota.sensor.core.entities.flows;


import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import pl.lasota.sensor.core.entities.Member;

import java.time.OffsetDateTime;

@Entity
@Table(name = "flow")
@Data
@NoArgsConstructor
public class Flow {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flow_seq")
    @SequenceGenerator(name = "flow_seq", sequenceName = "flow_seq", allocationSize = 1)
    private Long id;

    @Column(name = "activate")
    private boolean isActivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @Column(name = "config", columnDefinition = "jsonb", length = 100000)
    @Type(value = JsonBinaryType.class)
    private String config;

    @Column(name = "name", unique = true, length = 40)
    private String name;

    @Column(name = "server_ip", unique = true, length = 40)
    private String serverIp;

    @Column(name = "server_id", unique = true, length = 255)
    private String serverId;

    @Column(name = "updated")
    private OffsetDateTime updated;

}
