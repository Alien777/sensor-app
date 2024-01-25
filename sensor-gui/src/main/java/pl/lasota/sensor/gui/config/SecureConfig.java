package pl.lasota.sensor.gui.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import pl.lasota.sensor.gui.auth.ExceptionHandlingAuthentication;
import pl.lasota.sensor.gui.auth.SensorAuthenticationSuccessHandler;
import pl.lasota.sensor.gui.auth.filters.JwtOncePerRequestFilter;
import pl.lasota.sensor.gui.config.properties.SensorProperties;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecureConfig {

    private final JwtOncePerRequestFilter jwtOncePerRequestFilter;
    private final SensorProperties properties;
    private final ExceptionHandlingAuthentication authenticationEntryPoint;
    private final SensorAuthenticationSuccessHandler sensorAuthenticationSuccessHandler;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedOrigin(properties.getGuiUrl());
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedHeader("*");
                    return corsConfiguration;
                }))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/auth/token")
                            .permitAll();
                    auth.anyRequest()
                            .authenticated();
                })
                .logout(AbstractHttpConfigurer::disable)

                .oauth2Login(a -> a
                        .successHandler(sensorAuthenticationSuccessHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(withDefaults())
                .addFilterBefore(jwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}