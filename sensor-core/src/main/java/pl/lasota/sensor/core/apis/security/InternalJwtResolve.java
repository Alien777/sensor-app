package pl.lasota.sensor.core.apis.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.configs.CoreProperties;
import pl.lasota.sensor.core.exceptions.InternalAuthException;
import pl.lasota.sensor.core.entities.Role;
import pl.lasota.sensor.core.common.User;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class InternalJwtResolve {
    private final CoreProperties coreProperties;


    public String generateToken(String id, Collection<String> roles) {
        Long expiration = coreProperties.getJwtInternal()
                .expiration();

        SecretKey secretAsKey = coreProperties.getJwtInternal()
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

        SecretKey secretAsKey = coreProperties.getJwtInternal()
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
