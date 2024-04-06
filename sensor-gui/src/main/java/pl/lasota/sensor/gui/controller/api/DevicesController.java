package pl.lasota.sensor.gui.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.exceptions.*;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.entities.device.DeviceConfig;
import pl.lasota.sensor.core.apis.model.sensor.SendConfig;
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.service.DeviceService;
import pl.lasota.sensor.core.service.DeviceUtilsService;
import pl.lasota.sensor.core.service.MemberService;
import pl.lasota.sensor.gui.model.ConfigSaveT;
import pl.lasota.sensor.gui.model.ConfigT;
import pl.lasota.sensor.gui.model.DeviceSaveT;
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

    private final DeviceUtilsService dsu;

    private final MemberService ms;

    private final SensorMicroserviceEndpoint sae;


    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public String saveConfig(@RequestBody DeviceSaveT device) throws NotFoundMemberException {
        Member member = ms.loggedMember();
        return ds.save(member.getId(),device.getId(),device.getName());
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DeviceT> devices() throws NotFoundMemberException {
        Member member = ms.loggedMember();
        return ds.getAllDeviceBy(member.getId()).parallelStream()
                .map(device -> DeviceT.map(device, ds.hasConfig(device.getId())))
                .collect(Collectors.toList());
    }


    @PutMapping("/{id}/config/{id_config}/activate")
    @PreAuthorize("isAuthenticated()")
    public void activateConfig(@PathVariable("id") String deviceId, @PathVariable("id_config") Long configId) throws NotFoundDeviceException,
            NotFoundDefaultConfigException, ConfigParserException, NotFoundSchemaConfigException, JsonProcessingException, NotFoundMemberException {
        Member member = ms.loggedMember();
        ds.activateConfig(member.getId(), deviceId, configId);
        try {
            sae.sendConfigToDevice(new SendConfig(deviceId));
        } catch (Exception e) {
            log.error("Config sensor was save but not send to device", e);
        }
    }


    @GetMapping("/{id}/config/version")
    @PreAuthorize("isAuthenticated()")
    public List<ConfigT> allConfigs(@PathVariable("id") String deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, NotFoundMemberException {
        Member member = ms.loggedMember();
        List<ConfigT> configTS = new ArrayList<>();
        for (DeviceConfig d : ds.getConfigForDevice(member.getId(), deviceId)) {
            configTS.add(ConfigT.map(d, dsu.schemaForVersion(d.getForVersion())));
        }
        return configTS;
    }

    @GetMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT currentConfig(@PathVariable("id") String deviceId) throws NotFoundDeviceException, NotFoundDeviceConfigException, NotFoundSchemaConfigException, NotFoundMemberException {
        Member member = ms.loggedMember();
        DeviceConfig deviceConfig = ds.currentDeviceConfig(member.getId(), deviceId);
        return ConfigT.map(deviceConfig, dsu.schemaForVersion(deviceConfig.getForVersion()));
    }

    @PostMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT saveConfig(@RequestBody ConfigSaveT config, @PathVariable("id") String deviceId) throws NotFoundDeviceException, NotFoundSchemaConfigException, ConfigParserException, ConfigCheckSumExistException, NotFoundMemberException {
        Member member = ms.loggedMember();
        DeviceConfig deviceConfig = ds.saveConfig(member.getId(), config.getConfig(), config.getVersion(), deviceId);
        return ConfigT.map(deviceConfig, dsu.schemaForVersion(deviceConfig.getForVersion()));
    }

    @GetMapping("/config/schema/{version}")
    @PreAuthorize("isAuthenticated()")
    public String schemaForVersion(@PathVariable("version") String schemaVersion) throws NotFoundSchemaConfigException {
        return dsu.schemaForVersion(schemaVersion);
    }

}
