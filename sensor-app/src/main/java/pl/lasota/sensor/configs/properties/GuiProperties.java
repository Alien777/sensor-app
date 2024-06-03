package pl.lasota.sensor.configs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "sensor.gui")
public class GuiProperties {
    private JwtProperties jwt;
    private RedirectProperties redirect;
    private String guiUrl;
}
