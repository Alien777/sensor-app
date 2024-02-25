package pl.lasota.sensor.gui.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.models.Member;
import pl.lasota.sensor.core.models.device.DeviceConfig;
import pl.lasota.sensor.core.models.rest.SendConfigS;
import pl.lasota.sensor.core.models.rest.SensorApiEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceServiceUtils;
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
@Slf4j
public class DevicesController {

    private final DeviceService ds;

    private final DeviceServiceUtils dsu;

    private final MemberService ms;

    private final SensorApiEndpoint sae;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DeviceT> devices() {
        Member member = ms.loggedUser();
        return ds.getAllDeviceBy(member.getId()).parallelStream()
                .map(device -> DeviceT.map(device, ds.hasConfig(device.getId())))
                .collect(Collectors.toList());
    }


    @PutMapping("/{id}/config/{id_config}/activate")
    @PreAuthorize("isAuthenticated()")
    public void activateConfig(@PathVariable("id") Long deviceId, @PathVariable("id_config") Long configId) throws NotFoundDeviceException,
            NotFoundDefaultConfigException, ConfigParserException, NotFoundSchemaConfigException, JsonProcessingException {
        Member member = ms.loggedUser();
        ds.activateConfig(member.getId(), deviceId, configId);
        try {
            String deviceKey = ds.getDeviceKey(member.getId(), deviceId);
            sae.setupConfig(new SendConfigS(member.getMemberKey(), deviceKey));
        } catch (Exception e) {
            log.error("Config sensor was save but not send to device", e);
        }
    }


    @GetMapping("/{id}/config/version")
    @PreAuthorize("isAuthenticated()")
    public List<ConfigT> allConfigs(@PathVariable("id") Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException {
        Member member = ms.loggedUser();
        List<ConfigT> configTS = new ArrayList<>();
        for (DeviceConfig d : ds.getConfigForDevice(member.getId(), deviceId)) {
            configTS.add(ConfigT.map(d, dsu.schemaForVersion(d.getForVersion())));
        }
        return configTS;
    }

    @GetMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT currentConfig(@PathVariable("id") Integer deviceId) throws NotFoundDeviceException, NotFoundDeviceConfigException, NotFoundSchemaConfigException {
        Member member = ms.loggedUser();
        DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getId(), Long.valueOf(deviceId));
        return ConfigT.map(deviceConfig, dsu.schemaForVersion(deviceConfig.getForVersion()));
    }

    @PostMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT saveConfig(@RequestBody ConfigSaveT config, @PathVariable("id") Long deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException, ConfigCheckSumExistException {
        Member member = ms.loggedUser();
        DeviceConfig deviceConfig = ds.saveConfig(member.getId(), config.getConfig(), config.getVersion(), deviceId);
        return ConfigT.map(deviceConfig, dsu.schemaForVersion(deviceConfig.getForVersion()));
    }

    @GetMapping("/config/schema/{version}")
    @PreAuthorize("isAuthenticated()")
    public String schemaForVersion(@PathVariable("version") String schemaVersion) throws NotFoundSchemaConfigException {
        return dsu.schemaForVersion(schemaVersion);
    }

}
