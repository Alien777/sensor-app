package pl.lasota.sensor.api.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.apis.security.InternalAuthService;
import pl.lasota.sensor.core.apis.security.InternalConfigSecurity;
import pl.lasota.sensor.core.apis.security.InternalJwtOncePerRequestFilter;

import java.util.List;

@Configuration
@Service
@RequiredArgsConstructor
public class ApplicationSecurity {

    private final InternalAuthService internalAuthService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        InternalConfigSecurity internalConfigSecurity = new InternalConfigSecurity(new InternalJwtOncePerRequestFilter(internalAuthService, List.of("/api/sensor/message")));
        return internalConfigSecurity.securityFilterChain(http)
                .authorizeHttpRequests(r -> r.requestMatchers("/api/sensor/message").permitAll().anyRequest().authenticated())
                .build();
    }
}