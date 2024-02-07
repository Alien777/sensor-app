package pl.lasota.sensor.gui.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lasota.sensor.core.exceptions.SensorException;
import pl.lasota.sensor.gui.config.properties.SensorProperties;
import pl.lasota.sensor.gui.model.ErrorResponseT;

import java.io.IOException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements AuthenticationEntryPoint {

    private final SensorProperties properties;

    @ExceptionHandler(value = SensorException.class)
    public ResponseEntity<ErrorResponseT> blogNotFoundException(SensorException se) {
        ErrorResponseT errorResponseT = new ErrorResponseT(se.getCode(), se.getDetails());
        log.error("Sensor exception error {}, {}", se.getCode(), se.getDetails(), se);
        return new ResponseEntity<>(errorResponseT, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponseT> databaseConnectionFailsException(Exception e) {
        log.error("Internal error ", e);
        ErrorResponseT errorResponseT = new ErrorResponseT("EX", "Contact with admin");
        log.error("Sensor exception error {}", "EX", e);
        return new ResponseEntity<>(errorResponseT, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Void> handleException(AuthenticationException e) {
        log.error("Auth exception {}", "AU", e);
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Occurred problem with auth going to redirect", authException);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
