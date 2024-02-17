package pl.lasota.sensor.core.models.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("sensor-api")
@RestController
public interface SensorApiEndpoint {
    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/config")
    void setupConfig(@RequestBody SendConfigS configS) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/send/pwm")
    void pwmValue(@RequestBody SendPwmS configS) throws Exception;

}
