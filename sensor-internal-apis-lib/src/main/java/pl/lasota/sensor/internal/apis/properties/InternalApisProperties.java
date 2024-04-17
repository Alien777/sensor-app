package pl.lasota.sensor.internal.apis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( "sensor.internal-apis")
@Data
public class InternalApisProperties {
    private JwtInternalProperties jwt;
}
