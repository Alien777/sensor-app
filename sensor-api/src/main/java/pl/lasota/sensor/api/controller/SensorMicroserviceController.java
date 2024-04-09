package pl.lasota.sensor.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.mqtt.MqttPreSendLayout;
import pl.lasota.sensor.core.apis.model.sensor.SendConfig;
import pl.lasota.sensor.core.apis.model.sensor.SendPwm;
import pl.lasota.sensor.core.apis.SensorMicroserviceEndpoint;
import pl.lasota.sensor.core.entities.Member;
import pl.lasota.sensor.core.service.MemberService;


@RestController
@RequiredArgsConstructor
public class SensorMicroserviceController implements SensorMicroserviceEndpoint {

    private final MqttPreSendLayout mqttPreSendLayout;
    private final MemberService ms;

    @Override
    public void sendConfigToDevice(SendConfig configS) throws Exception {
        Member member = ms.loggedMember();
        mqttPreSendLayout.sendConfig(member.getId(), configS.deviceId());
    }

    @Override
    public void sendPwmValueToDevice(SendPwm configS) throws Exception {
        Member member = ms.loggedMember();
        mqttPreSendLayout.sendPwm(member.getId(), configS.deviceId(), configS.pin(), configS.value());
    }

}
