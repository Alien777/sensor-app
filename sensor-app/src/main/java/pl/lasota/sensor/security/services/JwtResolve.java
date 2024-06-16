package pl.lasota.sensor.security.services;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.configs.properties.GuiProperties;
import pl.lasota.sensor.entities.Member;
import pl.lasota.sensor.exceptions.AuthException;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtResolve {
    private final GuiProperties guiProperties;

    public String generateToken(Member user) {
        Long expiration = guiProperties.getJwt()
                .expiration();

        SecretKey secretAsKey = guiProperties.getJwt()
                .getSecretAsKey();
        return Jwts.builder()
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + (expiration * 60000)))
                .signWith(secretAsKey, Jwts.SIG.HS256)
                .subject(user.getUsername())
                .compact();
    }

    public Jws<Claims> parseToken(String token) throws AuthException {

        SecretKey secretAsKey = guiProperties.getJwt()
                .getSecretAsKey();
        try {
            return Jwts.parser()
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
