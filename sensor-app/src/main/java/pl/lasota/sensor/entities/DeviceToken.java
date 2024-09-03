package pl.lasota.sensor.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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

    @Column(name = "member_id")
    private String member;

    @Column(name = "token", unique = true)
    private UUID token;
}
