package pl.lasota.sensor.api.rest;

import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.core.models.rest.SendConfigS;
import pl.lasota.sensor.core.models.rest.SendPwmS;
import pl.lasota.sensor.core.models.rest.SensorApiEndpoint;


@RestController
public class SensorApiController implements SensorApiEndpoint {

    private final MqttPreSendLayout mqttPreSendLayout;

    public SensorApiController(MqttPreSendLayout mqttPreSendLayout) {
        this.mqttPreSendLayout = mqttPreSendLayout;
    }

    @Override
    public void setupConfig(SendConfigS configS) throws Exception {
        mqttPreSendLayout.sendConfig(configS.memberKey(), configS.deviceKey());
    }

    @Override
    public void pwmValue(SendPwmS configS) throws Exception {
        mqttPreSendLayout.sendPwm(configS.deviceKey(), configS.memberKey(), configS.pin(), configS.value());
    }

}
