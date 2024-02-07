package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.exceptions.ConfigParserException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceConfigException;
import pl.lasota.sensor.core.exceptions.NotFoundDeviceException;
import pl.lasota.sensor.core.exceptions.NotFoundSchemaConfigException;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.gui.model.ConfigSaveT;
import pl.lasota.sensor.gui.model.ConfigT;
import pl.lasota.sensor.gui.model.DeviceT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController()
@RequestMapping("/api/device")
@RequiredArgsConstructor

public class DevicesController {

    private final DeviceService ds;

    private final MemberService ms;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DeviceT> devices() {
        Member member = ms.loggedUser();
        return ds.getAllDeviceBy(member.getMemberKey()).parallelStream()
                .map(device -> DeviceT.map(device, ds.hasConfig(device.getId())))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}/config/version")
    @PreAuthorize("isAuthenticated()")
    public List<ConfigT> allConfigs(@PathVariable("id") Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException {
        List<ConfigT> configTS = new ArrayList<>();
        for (DeviceConfig d : ds.getConfigForDevice(deviceId)) {
            configTS.add(ConfigT.map(d, ds.schemaForVersion(d.getForVersion())));
        }
        return configTS;
    }

    @GetMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT currentConfig(@PathVariable("id") Integer deviceId) throws NotFoundDeviceException, NotFoundDeviceConfigException, NotFoundSchemaConfigException {
        DeviceConfig deviceConfig = ds.currentDeviceConfig(Long.valueOf(deviceId));
        return ConfigT.map(deviceConfig, ds.schemaForVersion(deviceConfig.getForVersion()));
    }

    @PostMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT saveConfig(@RequestBody ConfigSaveT config, @PathVariable("id") Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException {
        DeviceConfig deviceConfig = ds.saveConfig(config.getConfig(), config.getVersion(), deviceId);
        return ConfigT.map(deviceConfig, ds.schemaForVersion(deviceConfig.getForVersion()));
    }

    @GetMapping("/config/schema/{version}")
    @PreAuthorize("isAuthenticated()")
    public String schemaForVersion(@PathVariable("version") String schemaVersion) throws NotFoundSchemaConfigException {
        return ds.schemaForVersion(schemaVersion);
    }

}
