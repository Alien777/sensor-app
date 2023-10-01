package pl.lasota.sensor.core.config;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class SecurityConfig {
    @PostConstruct
    public void securityProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
