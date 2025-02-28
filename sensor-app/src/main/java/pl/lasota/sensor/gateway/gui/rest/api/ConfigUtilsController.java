package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.device.DeviceConfigInterface;

import java.util.List;

@RestController
@RequestMapping("/api/config-utils")
@RequiredArgsConstructor
@Slf4j
public class ConfigUtilsController {

    private final DeviceConfigInterface sme;

    @GetMapping("/{device}/pwm/pins")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getPwmsPins(@PathVariable("device") String deviceId) {
        return sme.getConfigPwmPins(deviceId);
    }

    @GetMapping("/{device}/digital/pins")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getDigitalPins(@PathVariable("device") String deviceId) {
        return sme.getConfigDigitalPins(deviceId);
    }

    @GetMapping("/{device}/analog/pins")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getAnalogsPins(@PathVariable("device") String deviceId) {
        return sme.getConfigAnalogPins(deviceId);
    }

    @GetMapping("/{device}/message-type")
    @PreAuthorize("isAuthenticated()")
    public List<String> getAvailableMessageType(@PathVariable("device") String deviceId) {
        return sme.getConfigMessageType(deviceId);
    }
}
