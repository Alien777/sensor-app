package pl.lasota.sensor.gui.model;

import lombok.Data;
import pl.lasota.sensor.core.entities.device.Device;

@Data
public class DeviceT {

    private String id;
    private String version;
    private String name;
    private boolean hasConfig;
    private String token;

    public static DeviceT map(Device device, boolean hasConfig) {

        DeviceT deviceT = new DeviceT();
        deviceT.id = device.getId();
        deviceT.version = device.getVersion();
        deviceT.name = device.getName();
        deviceT.hasConfig = hasConfig;
        deviceT.token = device.getCurrentDeviceToken().getToken();
        return deviceT;

    }
}
