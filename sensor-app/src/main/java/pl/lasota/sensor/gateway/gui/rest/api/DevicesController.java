package pl.lasota.sensor.gateway.gui.rest.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.device.DeviceApiInterface;
import pl.lasota.sensor.device.model.ConfigCreateI;
import pl.lasota.sensor.device.model.ConfigI;
import pl.lasota.sensor.device.model.DeviceCreateI;
import pl.lasota.sensor.device.model.DeviceI;
import pl.lasota.sensor.gateway.gui.model.ConfigSaveT;
import pl.lasota.sensor.gateway.gui.model.ConfigT;
import pl.lasota.sensor.gateway.gui.model.DeviceSaveT;
import pl.lasota.sensor.gateway.gui.model.DeviceT;

import java.util.ArrayList;
import java.util.List;

@RestController()
@RequestMapping("/api/device")
@RequiredArgsConstructor
@Slf4j
public class DevicesController {


    private final DeviceApiInterface sme;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public String saveDevice(@RequestBody DeviceSaveT device) {
        return sme.save(new DeviceCreateI(device.getId(), device.getName()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<DeviceT> devices(@RequestParam("withNotActive") boolean withNotActive) {
        List<DeviceI> device = sme.get();
        List<DeviceI> iList = new ArrayList<>(device);
        if (withNotActive) {
            List<DeviceI> temp = sme.getTemporaries();
            iList.addAll(temp);
        }
        return iList.stream().map(DeviceT::map).toList();
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
