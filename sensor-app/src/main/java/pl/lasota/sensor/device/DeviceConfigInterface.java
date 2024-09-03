package pl.lasota.sensor.device;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import pl.lasota.sensor.device.model.ConfigCreateI;
import pl.lasota.sensor.device.model.ConfigI;

import java.util.List;


public interface DeviceConfigInterface {

    void activateConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") Long configId);

    List<ConfigI> getConfigs(@RequestParam(value = "deviceId") String deviceId);

    ConfigI getConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") String configId);

    ConfigI getCurrentConfig(@RequestParam(value = "deviceId") String deviceId);

    void saveConfig(@RequestParam(value = "deviceId") String deviceId, @RequestBody ConfigCreateI configCreate);

    List<Integer> getConfigPwmPins(@RequestParam(value = "deviceId") String deviceId);

    List<Integer> getConfigAnalogPins(@RequestParam(value = "deviceId") String deviceId);

    List<String> getConfigMessageType(@RequestParam(value = "deviceId") String deviceId);

    List<Integer> getConfigDigitalPins(String deviceId);
}
