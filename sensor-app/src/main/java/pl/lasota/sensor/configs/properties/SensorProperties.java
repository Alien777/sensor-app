package pl.lasota.sensor.configs.properties;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties("sensor")
@Data
@Component
@NoArgsConstructor
public class SensorProperties {

    private FlowsProperties flowsProperties;
    private DeviceProperties deviceProperties;
    private GuiProperties guiProperties;
    private AIProperties aiProperties;
}
