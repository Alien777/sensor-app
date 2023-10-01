package pl.lasota.sensor.api.mqtt;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.core.model.Message;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessagePublish {

    private final IMqttClient iMqttClient;

    public void publish(Message message) throws MqttException, JsonProcessingException {
        String json = message.toRawJson();
        iMqttClient.publish("/" + message.getMemberKey() + "/" + message.getDeviceKey(), new MqttMessage(json.getBytes()));
        log.info("Sent message  {}", json);
    }

}
