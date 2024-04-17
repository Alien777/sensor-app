package pl.lasota.sensor.gui.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import pl.lasota.sensor.internal.apis.properties.InternalApisProperties;


@Data
@Configuration
@ConfigurationProperties(prefix = "sensor")
public class GuiProperties {
    private InternalApisProperties jwtInternalProperties;
    private JwtProperties jwt;
    private RedirectProperties redirect;
    private String guiUrl;
    private String mqttIp;
}
