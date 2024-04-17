package pl.lasota.sensor.api.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MqttProperties {
    private String clientId;
    private String url;
    private String topic;
    private String username;
    private String password;
    private Integer maxReconnectDelay;
    private Integer connectionTimeout;
    private Boolean automaticReconnect;
    private Boolean unsecureRestEndpoint;
}
