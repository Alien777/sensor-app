package pl.lasota.sensor.api.apis.mappper;

import pl.lasota.sensor.api.entities.Device;
import pl.lasota.sensor.api.entities.DeviceConfig;
import pl.lasota.sensor.api.entities.DeviceTemporary;
import pl.lasota.sensor.internal.apis.api.device.ConfigI;
import pl.lasota.sensor.internal.apis.api.device.DeviceI;

public final class Mapper {
    public static DeviceI map(Device device) {
        return new DeviceI(device.getId(), device.getVersion(), device.getName(), device.getCurrentDeviceConfig() != null, device.getCurrentDeviceToken().getToken());
    }

    public static DeviceI map(DeviceTemporary device) {
        return new DeviceI(device.getId().toString(), null, device.getName(), false, device.getCurrentDeviceToken().getToken());
    }

    public static ConfigI map(DeviceConfig config) {
        return new ConfigI(config.getId(), config.getConfig(), config.getForVersion(), config.getTime());
    }
}
