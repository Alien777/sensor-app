package pl.lasota.sensor.gateway.gui.rest;

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
import pl.lasota.sensor.exceptions.SensorException;
import pl.lasota.sensor.gateway.gui.model.ErrorResponseT;

import java.io.IOException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler implements AuthenticationEntryPoint {

    @ExceptionHandler(value = SensorException.class)
    public ResponseEntity<ErrorResponseT> blogNotFoundException(SensorException se) {
        ErrorResponseT errorResponseT = new ErrorResponseT(se.getMessage(), se.getDetails());
        log.error("Sensor exception error {}, {}", se.getMessage(), se.getDetails(), se);
        return new ResponseEntity<>(errorResponseT, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponseT> databaseConnectionFailsException(Exception e) {
        log.error("Internal error ", e);
        ErrorResponseT errorResponseT = new ErrorResponseT(e.getMessage(), "Contact with admin");
        log.error("Sensor exception error {}", e.getMessage(), e);
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
