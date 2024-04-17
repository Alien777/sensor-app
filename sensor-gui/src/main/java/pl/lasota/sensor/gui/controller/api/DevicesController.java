package pl.lasota.sensor.gui.controller.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.gui.model.ConfigSaveT;
import pl.lasota.sensor.gui.model.ConfigT;
import pl.lasota.sensor.gui.model.DeviceSaveT;
import pl.lasota.sensor.gui.model.DeviceT;
import pl.lasota.sensor.internal.apis.api.SensorMicroserviceEndpoint;
import pl.lasota.sensor.internal.apis.api.device.ConfigCreateI;
import pl.lasota.sensor.internal.apis.api.device.ConfigI;
import pl.lasota.sensor.internal.apis.api.device.DeviceCreateI;
import pl.lasota.sensor.internal.apis.api.device.DeviceI;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
public class DevicesController {


    private final SensorMicroserviceEndpoint sme;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public String saveDevice(@RequestBody DeviceSaveT device) {
        return sme.save(new DeviceCreateI(device.getId(), device.getName()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DeviceT> devices(@RequestParam("withNotActive") boolean withNotActive) {
        List<DeviceI> device = sme.get();
        if (withNotActive) {
            List<DeviceI> temp = sme.getTemporaries();
            device.addAll(temp);
        }
        return device.stream().map(DeviceT::map).toList();
    }


    @PutMapping("/{id}/config/{id_config}/activate")
    @PreAuthorize("isAuthenticated()")
    public void activateConfig(@PathVariable("id") String deviceId, @PathVariable("id_config") Long configId) {
        sme.activateConfig(deviceId, configId);
    }


    @GetMapping("/{id}/config/version")
    @PreAuthorize("isAuthenticated()")
    public List<ConfigT> allConfigs(@PathVariable("id") String deviceId) {
        List<ConfigT> configTS = new ArrayList<>();
        for (ConfigI configI : sme.getConfigs(deviceId)) {
            configTS.add(ConfigT.map(configI, sme.getSchema(configI.forVersion())));
        }
        return configTS;
    }

    @GetMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public ConfigT currentConfig(@PathVariable("id") String deviceId) {
        ConfigI configI = sme.getCurrentConfig(deviceId);
        String schema = sme.getSchema(configI.forVersion());
        return ConfigT.map(configI, schema);
    }

    @PostMapping("/{id}/config")
    @PreAuthorize("isAuthenticated()")
    public void saveConfig(@RequestBody ConfigSaveT config, @PathVariable("id") String deviceId) {
        sme.saveConfig(deviceId, new ConfigCreateI(config.getVersion(), config.getConfig()));
    }

    @GetMapping("/config/schema/{version}")
    @PreAuthorize("isAuthenticated()")
    public String schemaForVersion(@PathVariable("version") String schemaVersion) {
        return sme.getSchema(schemaVersion);
    }
}
