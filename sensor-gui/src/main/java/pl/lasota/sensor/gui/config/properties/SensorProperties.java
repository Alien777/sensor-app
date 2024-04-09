package pl.lasota.sensor.gui.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "sensor")
public class SensorProperties {
    private JwtProperties jwt;
    private RedirectProperties redirect;
    private String guiUrl;
    private String mqttIp;
}
