package pl.lasota.sensor.api.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.mqtt.Utils;
import pl.lasota.sensor.core.models.rest.SendConfigS;
import pl.lasota.sensor.core.models.rest.SensorApiEndpoint;



@RestController
public class SensorApiController implements SensorApiEndpoint {

    private final Utils utils;

    public SensorApiController(Utils utils) {
        this.utils = utils;
    }

    @Override
    public void setupConfig(SendConfigS configS) throws Exception {
        utils.sendConfig(configS.memberKey(), configS.deviceKey());
    }

}
