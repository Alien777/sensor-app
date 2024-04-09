package pl.lasota.sensor.api.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.SimpleCallback;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttConfig {
    private final MqttProperties properties;
    @Bean
    public IMqttClient mqttClient() throws MqttException {
        return new MqttClient(properties.getUrl(), properties.getClientId());
    }

    @Bean("connected")
    public SimpleCallback connected(IMqttClient mqttClient) {
        return () -> mqttClient.subscribe(properties.getTopic(), 2);
    }

    @Bean("disconnected")
    public SimpleCallback disconnected(IMqttClient mqttClient) {
        return () -> mqttClient.unsubscribe(properties.getTopic());
    }

}

