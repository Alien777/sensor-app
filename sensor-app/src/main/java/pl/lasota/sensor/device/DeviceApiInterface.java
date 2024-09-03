package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.lasota.sensor.device.model.DeviceBuildI;
import pl.lasota.sensor.device.model.DeviceCreateI;
import pl.lasota.sensor.device.model.DeviceI;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface DeviceApiInterface {

    UUID save(@RequestBody DeviceCreateI deviceCreateI);

    List<DeviceI> get();

    DeviceI get(@RequestParam(value = "deviceId") String deviceId);

    List<DeviceI> getTemporaries();

    String getSchema(@RequestParam(value = "versionDevice") String versionDevice);

    String getMqttIp();

    byte[] build(DeviceBuildI device) throws IOException;

    List<String> getVersions();



}
