package pl.lasota.sensor.configs;

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
import pl.lasota.sensor.gateway.gui.rest.GlobalExceptionHandler;
import pl.lasota.sensor.security.JwtOncePerRequestFilter;
import pl.lasota.sensor.security.SensorAuthenticationSuccessOAuthHandler;
import pl.lasota.sensor.configs.properties.GuiProperties;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableWebSecurity
public class SecureConfig {

    private final JwtOncePerRequestFilter jwtOncePerRequestFilter;
    private final GlobalExceptionHandler authenticationEntryPoint;
    private final SensorAuthenticationSuccessOAuthHandler sensorAuthenticationSuccessOAuthHandler;

    private final GuiProperties properties;

    public final static String[] OPENED_PATHS = {"/api/auth/token", "/sererver/"};
    public final static String LOGIN_PATH = "/api/auth/login";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

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
                    auth.requestMatchers(OPENED_PATHS)
                            .permitAll();
                    auth.anyRequest()
                            .authenticated();
                })
                .logout(AbstractHttpConfigurer::disable)
                .oauth2Login(a -> a
                        .successHandler(sensorAuthenticationSuccessOAuthHandler))
                .httpBasic(withDefaults())
                .addFilterBefore(jwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }
}