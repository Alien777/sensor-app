package pl.lasota.sensor.core.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("sensor.core")
@Data
public class CoreProperties {
    private String firmwareFolder;
    private String sensorFlowsName;
}
