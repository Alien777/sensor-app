package pl.lasota.sensor.core.apis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.apis.model.sensor.SendConfig;
import pl.lasota.sensor.core.apis.model.sensor.SendPwm;
import pl.lasota.sensor.core.apis.security.InternalClientInterceptorConfiguration;

@FeignClient(name = "sensor-api", configuration = InternalClientInterceptorConfiguration.class)
public interface SensorMicroserviceEndpoint {
    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/config")
    void sendConfigToDevice(@RequestBody SendConfig configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/pwm")
    void sendPwmValueToDevice(@RequestBody SendPwm configS) throws Exception;


}
