package pl.lasota.sensor.flows.configs;

import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public record JwtInternalProperties(String secret, Long expiration) {
    public SecretKey getSecretAsKey() {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        return new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
    }
}