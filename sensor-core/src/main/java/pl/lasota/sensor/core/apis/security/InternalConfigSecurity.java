package pl.lasota.sensor.core.apis.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;


@RequiredArgsConstructor
public class InternalConfigSecurity {

    private final InternalJwtOncePerRequestFilter internalJwtOncePerRequestFilter;

    public HttpSecurity securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedOrigin("*");
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedHeader("*");
                    return corsConfiguration;
                }))
                .httpBasic(withDefaults())
                .addFilterBefore(internalJwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}