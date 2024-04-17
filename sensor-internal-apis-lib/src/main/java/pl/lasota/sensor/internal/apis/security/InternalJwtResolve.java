package pl.lasota.sensor.internal.apis.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.internal.apis.exceptions.InternalAuthException;
import pl.lasota.sensor.internal.apis.properties.InternalApisProperties;
import pl.lasota.sensor.member.User;
import pl.lasota.sensor.member.entities.Role;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class InternalJwtResolve {
    private final InternalApisProperties internalApisProperties;


    public String generateToken(String id, Collection<String> roles) {
        Long expiration = internalApisProperties.getJwt()
                .expiration();

        SecretKey secretAsKey = internalApisProperties.getJwt()
                .getSecretAsKey();
        return Jwts.builder()
                .claim("id", id)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 60000)))
                .signWith(secretAsKey, Jwts.SIG.HS256)
                .subject(id.toString())
                .compact();
    }

    public User parseToken(String token) throws InternalAuthException {

        SecretKey secretAsKey = internalApisProperties.getJwt()
                .getSecretAsKey();
        try {
            Claims o = Jwts.parser()
                    .verifyWith(secretAsKey)
                    .critical()
                    .and()
                    .clock(Date::new)
                    .build()
                    .parseSignedClaims(token).getPayload();
            Collection<String> roles = (Collection<String>) o.get("roles");
            String id = o.get("id").toString();
            return User.builder().id(id).roles(roles.stream().map(Role::valueOf).collect(Collectors.toList())).isEnabled(true).build();
        } catch (Exception e) {
            throw new InternalAuthException("Not resolve token ", e);
        }
    }
}
