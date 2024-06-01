package pl.lasota.sensor.device;


import pl.lasota.sensor.device.model.ConfigI;
import pl.lasota.sensor.device.model.DeviceI;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;
import pl.lasota.sensor.entities.DeviceTemporary;


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
