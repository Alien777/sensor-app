package pl.lasota.sensor.core.restapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.models.rest.SendConfigS;
import pl.lasota.sensor.core.models.rest.SendPwmS;

@FeignClient("sensor-api")
public interface SensorApiEndpoint {
    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/config")
    void setupConfig(@RequestBody SendConfigS configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/pwm")
    void pwmValue(@RequestBody SendPwmS configS) throws Exception;

}
