package pl.lasota.sensor.api.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.mqttPayloads.MessageFrame;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessagePublish {

    private final IMqttClient iMqttClient;

    public void publish(MessageFrame messageFrame) throws MqttException, JsonProcessingException {
        String json = messageFrame.toJson();
        iMqttClient.publish("/" + messageFrame.getMemberKey() + "/" + messageFrame.getDeviceKey(), new MqttMessage(json.getBytes()));
        log.info("Sent messageFrame  {}", json);
    }

}
