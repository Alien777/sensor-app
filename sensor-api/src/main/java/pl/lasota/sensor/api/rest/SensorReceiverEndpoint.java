package pl.lasota.sensor.api.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.api.MessageReceiver;
import pl.lasota.sensor.api.configs.MqttProperties;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;

@RestController
@RequiredArgsConstructor
public class SensorReceiverEndpoint {
    private final MessageReceiver messageReceiver;
    private final MqttProperties mqttProperties;

    @RequestMapping(method = RequestMethod.POST, value = "/api/sensor/message")
    public void receiverMessage(@RequestBody MessageFrame messagePayload) {
        if (!mqttProperties.getUnsecureRestEndpoint()) {
            return;
        }
        messageReceiver.received(messagePayload);
    }

}
