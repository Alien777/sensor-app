package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.lasota.sensor.device.model.DeviceCreateI;
import pl.lasota.sensor.device.model.DeviceI;
import pl.lasota.sensor.entities.Device;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


public interface DeviceApiInterface {

    String save(@RequestBody DeviceCreateI deviceCreateI);

    List<DeviceI> get();

    DeviceI get(@RequestParam(value = "deviceId") String deviceId);

    List<DeviceI> getTemporaries();

    String getSchema(@RequestParam(value = "versionDevice") String versionDevice);

    String getMqttIp();

    Optional<Device> getDevice(String memberId, String deviceId);

    byte[] build(String version, String name, String ssid, String password) throws IOException;

    List<String> getVersions();


}
