package pl.lasota.sensor.gateway.device;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.payload.MessageFrame;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessagePublish {

    private final IMqttClient iMqttClient;

    public void publish(MessageFrame messageFrame) throws MqttException, JsonProcessingException {
        iMqttClient.publish("/" + messageFrame.getMemberId() + "/" + messageFrame.getDeviceId(), new MqttMessage(messageFrame.convert().getBytes(StandardCharsets.UTF_8)));
        log.info("Sent messageFrame  {}", messageFrame.getMessageType());
    }

}
