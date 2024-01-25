package pl.lasota.sensor.gui.model;

import lombok.Data;
import pl.lasota.sensor.core.models.device.Device;

@Data
public class DeviceT {

    private Long id;
    private String version;
    private String deviceKey;
    private String name;

    public static DeviceT map(Device device) {

        DeviceT deviceT = new DeviceT();
        deviceT.id = device.getId();
        deviceT.version = device.getVersion();
        deviceT.deviceKey = device.getDeviceKey();
        deviceT.name = device.getName();
        return deviceT;

    }
}
