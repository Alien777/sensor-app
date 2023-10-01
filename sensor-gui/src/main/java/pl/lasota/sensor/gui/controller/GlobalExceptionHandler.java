package pl.lasota.sensor.gui.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.lasota.sensor.gui.config.properties.SensorProperties;
import pl.lasota.sensor.gui.exceptions.SensorException;
import pl.lasota.sensor.gui.exceptions.SensorGuiException;

import java.util.UUID;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SensorProperties properties;

    @ExceptionHandler(value = {AuthenticationException.class})
    public String handleException(AuthenticationException e) {
        log.error("Not access for source ", e);
        return "redirect:" + properties.getRedirect()
                .authException();
    }

    @ExceptionHandler(value = {SensorException.class})
    public String handleException(SensorException e) throws SensorGuiException {
        String uuidError = UUID.randomUUID()
                .toString();
        log.error("Problem with execute endpoint {} ", uuidError, e);
        throw new SensorGuiException(uuidError);
    }
}
