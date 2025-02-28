package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.device.DeviceApiInterface;

@RestController()
@RequestMapping("/api/resource")
@RequiredArgsConstructor
@Slf4j
public class ResourceController {


    private final DeviceApiInterface sme;

    @GetMapping("/mqtt-server")
    @PreAuthorize("isAuthenticated()")
    public String schemaForVersion() {
        return sme.getMqttIp();
    }

}
