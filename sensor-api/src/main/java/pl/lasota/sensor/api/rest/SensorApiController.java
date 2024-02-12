package pl.lasota.sensor.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.mqtt.Utils;
import pl.lasota.sensor.core.models.rest.SendConfigS;

import static pl.lasota.sensor.core.models.rest.InternalAddressUrl.SEND_CONFIG_TO_DEVICE;

@RestController
@RequiredArgsConstructor
public class SensorApiController {

    private final Utils utils;

    @PostMapping(SEND_CONFIG_TO_DEVICE)
    public void sendCurrentConfig(@RequestBody SendConfigS sendConfigS) throws Exception {
        utils.sendConfig(sendConfigS.memberKey(), sendConfigS.deviceKey());
    }
}
