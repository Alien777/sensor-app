package pl.lasota.sensor.gui.security;


import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.gui.config.properties.GuiProperties;
import pl.lasota.sensor.gui.exceptions.AuthException;
import pl.lasota.sensor.member.User;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtResolve {
    private final GuiProperties guiProperties;

    public String generateToken(User user) {
        Long expiration = guiProperties.getJwt()
                .expiration();

        SecretKey secretAsKey = guiProperties.getJwt()
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

        SecretKey secretAsKey = guiProperties.getJwt()
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
