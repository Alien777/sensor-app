package pl.lasota.sensor.api.controller;

import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.core.models.rest.SendConfigS;
import pl.lasota.sensor.core.models.rest.SendPwmS;
import pl.lasota.sensor.core.restapi.SensorApiEndpoint;


@RestController
public class SensorApiController implements SensorApiEndpoint {

    private final MqttPreSendLayout mqttPreSendLayout;

    public SensorApiController(MqttPreSendLayout mqttPreSendLayout) {
        this.mqttPreSendLayout = mqttPreSendLayout;
    }

    @Override
    public void sendConfigToDevice(SendConfigS configS) throws Exception {
        mqttPreSendLayout.sendConfig(configS.memberKey(), configS.deviceId());
    }

    @Override
    public void sendPwmValueToDevice(SendPwmS configS) throws Exception {
        mqttPreSendLayout.sendPwm(configS.memberKey(), configS.deviceId(), configS.pin(), configS.value());
    }

}
