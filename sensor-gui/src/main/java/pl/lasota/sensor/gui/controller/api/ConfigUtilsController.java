package pl.lasota.sensor.gui.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundMemberException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.mqtt.payload.to.AnalogConfig;
import pl.lasota.sensor.core.models.mqtt.payload.to.ConfigPayload;
import pl.lasota.sensor.core.models.mqtt.payload.to.PwmConfig;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.core.service.MemberService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/config-utils")
@RequiredArgsConstructor
@Slf4j
public class ConfigUtilsController {
    private final DeviceService ds;

    private final DeviceUtilsService dsu;

    private final MemberService ms;

    @GetMapping("/{device}/pwm/pins")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getPwmsPins(@PathVariable("device") String device) throws NotFoundMemberException, NotFoundDeviceConfigException, NotFoundDeviceException, JsonProcessingException {
        Member member = ms.loggedUser();
        DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getMemberKey(), device);
        ConfigPayload configPayload = dsu.mapConfigToObject(deviceConfig.getConfig());
        return configPayload.getPwmConfig().stream().map(PwmConfig::getPin).collect(Collectors.toList());
    }

    @GetMapping("/{device}/analog/pins")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getAnalogsPins(@PathVariable("device") String device) throws NotFoundMemberException, NotFoundDeviceConfigException, NotFoundDeviceException, JsonProcessingException {
        Member member = ms.loggedUser();
        DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getMemberKey(), device);
        ConfigPayload configPayload = dsu.mapConfigToObject(deviceConfig.getConfig());
        return configPayload.getAnalogReader().stream().map(AnalogConfig::getPin).collect(Collectors.toList());
    }
}
