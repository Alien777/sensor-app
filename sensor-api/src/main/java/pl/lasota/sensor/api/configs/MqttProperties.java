package pl.lasota.sensor.api.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sensor.mqtt")
@Data
public class MqttProperties {

    private String clientId;
    private String url;
    private String topic;
    private String username;
    private String password;
    private Integer maxReconnectDelay;
    private Integer connectionTimeout;
    private Boolean automaticReconnect;
}
