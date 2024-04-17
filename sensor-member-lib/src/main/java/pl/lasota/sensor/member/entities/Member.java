package pl.lasota.sensor.member.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id
    @Column(name = "id", unique = true, length = 16)
    private String id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;
}
