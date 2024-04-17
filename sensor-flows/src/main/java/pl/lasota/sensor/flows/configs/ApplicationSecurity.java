package pl.lasota.sensor.flows.configs;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.internal.apis.security.InternalAuthService;
import pl.lasota.sensor.internal.apis.security.InternalConfigSecurity;
import pl.lasota.sensor.internal.apis.security.InternalJwtOncePerRequestFilter;

@Configuration
@Service
@RequiredArgsConstructor
public class ApplicationSecurity {

    private final InternalAuthService internalAuthService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        InternalConfigSecurity internalConfigSecurity = new InternalConfigSecurity(new InternalJwtOncePerRequestFilter(internalAuthService));
        return internalConfigSecurity.securityFilterChain(http).build();
    }
}