package pl.lasota.sensor.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.lasota.sensor.internal.apis.properties.InternalApisProperties;
import pl.lasota.sensor.internal.apis.properties.JwtInternalProperties;

@Configuration
@ConfigurationProperties("sensor.api")
@Data
public class ApiProperties {
    private JwtInternalProperties jwt;
    private String firmwareFolder;
    private MqttProperties mqtt;
    private InternalApisProperties internalApisProperties;
}
