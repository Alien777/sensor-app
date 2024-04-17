package pl.lasota.sensor.internal.apis.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;


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
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(internalJwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }
}