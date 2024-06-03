package pl.lasota.sensor.configs.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("sensor.flows")
@Data
@Component
@NoArgsConstructor
public class FlowsProperties {
    private String[] scanNodes;
}
