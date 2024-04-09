package pl.lasota.sensor.gui.model;

import lombok.Data;
import pl.lasota.sensor.core.entities.device.Device;
import pl.lasota.sensor.core.entities.device.DeviceTemporary;

import java.util.UUID;

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

    public static DeviceT map(DeviceTemporary device) {
        DeviceT deviceT = new DeviceT();
        deviceT.name = device.getName();
        deviceT.id = UUID.randomUUID().toString();
        deviceT.hasConfig = false;
        deviceT.token = device.getCurrentDeviceToken().getToken();
        return deviceT;
    }
}
