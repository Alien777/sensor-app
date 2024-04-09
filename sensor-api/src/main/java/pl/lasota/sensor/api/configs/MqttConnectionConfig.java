package pl.lasota.sensor.api.configs;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.api.mqtt.MqttCallbackHandler;

@Component
@RequiredArgsConstructor
public class MqttConnectionConfig {
    private final IMqttClient mqttClient;
    private final MqttProperties properties;
    private final MqttCallbackHandler callbackHandler;

    @PostConstruct
    public void init() throws MqttException {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(properties.getAutomaticReconnect());
        options.setCleanStart(true);
        options.setConnectionTimeout(properties.getConnectionTimeout());
        options.setMaxReconnectDelay(properties.getMaxReconnectDelay());
        options.setUserName(properties.getUsername());
        options.setPassword(properties.getPassword().getBytes());
        mqttClient.setCallback(callbackHandler);
        mqttClient.connect(options);

    }

}
