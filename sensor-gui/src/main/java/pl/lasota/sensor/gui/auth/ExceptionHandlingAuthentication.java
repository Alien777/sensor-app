package pl.lasota.sensor.gui.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.gui.config.properties.SensorProperties;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingAuthentication implements AuthenticationEntryPoint {

    private final SensorProperties sensorProperties;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Occurred problem with auth going to redirect", authException);
        response.sendRedirect(sensorProperties.getRedirect()
                .authException());
    }
}
