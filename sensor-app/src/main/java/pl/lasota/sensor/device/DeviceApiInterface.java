package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.lasota.sensor.device.model.*;
import pl.lasota.sensor.entities.Device;
import pl.lasota.sensor.entities.DeviceConfig;

import java.util.List;
import java.util.Optional;


public interface DeviceApiInterface {

    void sendConfigToDevice( SendConfigI configS) throws Exception;

    void sendPwmValueToDevice(@RequestBody SendPwmI configS) throws Exception;

    void sendRequestForDataAnalog(@RequestBody SendForAnalogDataI configS) throws Exception;

    String save(@RequestBody DeviceCreateI deviceCreateI);

    List<DeviceI> get();

    DeviceI get(@RequestParam(value = "deviceId") String deviceId);

    List<DeviceI> getTemporaries();

    void activateConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") Long configId);

    List<ConfigI> getConfigs(@RequestParam(value = "deviceId") String deviceId);

    ConfigI getConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") String configId);

    ConfigI getCurrentConfig(@RequestParam(value = "deviceId") String deviceId);

    String getSchema(@RequestParam(value = "versionDevice") String versionDevice);

    void saveConfig(@RequestParam(value = "deviceId") String deviceId, @RequestBody ConfigCreateI configCreate);

    List<Integer> getConfigPwmPins(@RequestParam(value = "deviceId") String deviceId);

    List<Integer> getConfigAnalogPins(@RequestParam(value = "deviceId") String deviceId);

    List<String> getConfigMessageType(@RequestParam(value = "deviceId") String deviceId);

    String getMqttIp();

    DeviceConfig currentDeviceConfig(String memberId, String deviceId);

    Optional<Device> getDevice(String memberId, String deviceId);
}
