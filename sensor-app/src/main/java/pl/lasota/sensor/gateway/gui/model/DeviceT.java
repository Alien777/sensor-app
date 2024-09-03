package pl.lasota.sensor.gateway.gui.model;

import lombok.Data;
import pl.lasota.sensor.device.model.DeviceI;

import java.util.UUID;


@Data
public class DeviceT {

    private String id;
    private String version;
    private String name;
    private boolean hasConfig;
    private UUID token;

    public static DeviceT map(DeviceI device) {
        DeviceT deviceT = new DeviceT();
        deviceT.id = device.id();
        deviceT.version = device.currentVersion();
        deviceT.name = device.name();
        deviceT.hasConfig = device.hasConfig();
        deviceT.token = device.token();
        return deviceT;
    }
}
