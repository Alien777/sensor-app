package pl.lasota.sensor.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "device_token")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_token_key_seq")
    @SequenceGenerator(name = "device_token_key_seq", sequenceName = "device_token_key_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @Column(name = "token", unique = true)
    private String token;

}
