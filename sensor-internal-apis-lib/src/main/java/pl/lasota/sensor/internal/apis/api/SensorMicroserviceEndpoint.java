package pl.lasota.sensor.internal.apis.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import pl.lasota.sensor.internal.apis.api.device.*;
import pl.lasota.sensor.internal.apis.security.InternalClientInterceptorConfiguration;

import java.util.List;


@FeignClient(name = "sensor-api", configuration = InternalClientInterceptorConfiguration.class)
public interface SensorMicroserviceEndpoint {
    static String path = "/api-internal/device";

    @RequestMapping(method = RequestMethod.POST, value = path + "/send/config")
    void sendConfigToDevice(@RequestBody SendConfigI configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = path + "/send/pwm")
    void sendPwmValueToDevice(@RequestBody SendPwmI configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = path + "/send/analog")
    void sendRequestForDataAnalog(@RequestBody SendForAnalogDataI configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = path)
    String save(@RequestBody DeviceCreateI deviceCreateI);

    @RequestMapping(method = RequestMethod.GET, value = path)
    List<DeviceI> get();

    @RequestMapping(method = RequestMethod.GET, path = path + "/{deviceId}")
    DeviceI get(@RequestParam(value = "deviceId") String deviceId);

    @RequestMapping(method = RequestMethod.GET, path = path + "/temporary")
    List<DeviceI> getTemporaries();

    @RequestMapping(method = RequestMethod.GET, path = path + "/{deviceId}/config/{configId}/activate")
    void activateConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") Long configId);

    @RequestMapping(method = RequestMethod.GET, path = path + "/{deviceId}/config")
    List<ConfigI> getConfigs(@RequestParam(value = "deviceId") String deviceId);

    @RequestMapping(method = RequestMethod.GET, path = path + "/{deviceId}/config/{configId}")
    ConfigI getConfig(@RequestParam(value = "deviceId") String deviceId, @RequestParam(value = "configId") String configId);

    @RequestMapping(method = RequestMethod.GET, path = path + "/{deviceId}/config/current")
    ConfigI getCurrentConfig(@RequestParam(value = "deviceId") String deviceId);

    @RequestMapping(method = RequestMethod.GET, path = path + "/schema/{versionDevice}")
    String getSchema(@RequestParam(value = "versionDevice") String versionDevice);

    @RequestMapping(method = RequestMethod.POST, value = path + "/{deviceId}/config")
    void saveConfig(@RequestParam(value = "deviceId") String deviceId, @RequestBody ConfigCreateI configCreate);

    @RequestMapping(method = RequestMethod.GET, value = path + "/{deviceId}/config/pwm/pins")
    List<Integer> getConfigPwmPins(@RequestParam(value = "deviceId") String deviceId);

    @RequestMapping(method = RequestMethod.GET, value = path + "/{deviceId}/config/analog/pins")
    List<Integer> getConfigAnalogPins(@RequestParam(value = "deviceId") String deviceId);

    @RequestMapping(method = RequestMethod.GET, value = path + "/{deviceId}/config/message-type")
    List<String> getConfigMessageType(@RequestParam(value = "deviceId") String deviceId);
}
