package pl.lasota.sensor.gui.security;


import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.common.User;
import pl.lasota.sensor.gui.config.properties.SensorProperties;
import pl.lasota.sensor.gui.exceptions.AuthException;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtResolve {
    private final SensorProperties sensorProperties;

    public String generateToken(User user) {
        Long expiration = sensorProperties.getJwt()
                .expiration();

        SecretKey secretAsKey = sensorProperties.getJwt()
                .getSecretAsKey();
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("roles", user.getRoles())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 60000)))
                .signWith(secretAsKey, Jwts.SIG.HS256)
                .subject(user.getUsername())
                .compact();
    }

    public void parseToken(String token) throws AuthException {

        SecretKey secretAsKey = sensorProperties.getJwt()
                .getSecretAsKey();
        try {
            Jwts.parser()
                    .verifyWith(secretAsKey)
                    .critical()
                    .and()
                    .clock(Date::new)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            throw new AuthException("Not resolve token ", e);
        }
    }
}