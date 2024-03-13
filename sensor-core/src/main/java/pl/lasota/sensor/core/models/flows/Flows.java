package pl.lasota.sensor.core.models.flows;


import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import pl.lasota.sensor.core.models.Member;

@Entity
@Table(name = "flow")
@Data
@NoArgsConstructor
public class Flows {

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

}
