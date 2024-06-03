package pl.lasota.sensor.configs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sensor.api")
@Data
public class ApiProperties {
    private String firmwareFolder;
    private MqttProperties mqtt;
    private JwtDeviceProperties jwtDeviceProperties;
}
