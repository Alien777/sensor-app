package pl.lasota.sensor.configs;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.mqttv5.client.IMqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.gateway.device.MqttCallbackHandler;
import pl.lasota.sensor.configs.properties.DeviceProperties;

@Component
@RequiredArgsConstructor
public class MqttConnectionConfig {
    private final IMqttClient mqttClient;
    private final DeviceProperties properties;
    private final MqttCallbackHandler callbackHandler;

    @PostConstruct
    public void init() throws MqttException {
        MqttConnectionOptions options = new MqttConnectionOptions();
        options.setAutomaticReconnect(properties.getMqtt().getAutomaticReconnect());
        options.setCleanStart(true);
        options.setConnectionTimeout(properties.getMqtt().getConnectionTimeout());
        options.setMaxReconnectDelay(properties.getMqtt().getMaxReconnectDelay());
        options.setUserName(properties.getMqtt().getUsername());
        options.setPassword(properties.getMqtt().getPassword().getBytes());
        mqttClient.setCallback(callbackHandler);
        mqttClient.connect(options);

    }

}
