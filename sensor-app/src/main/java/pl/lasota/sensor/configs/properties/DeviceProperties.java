package pl.lasota.sensor.configs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("sensor.device")
@Data
public class DeviceProperties {
    private String firmwareFolder;
    private String mqttIpExternal;
    private String generateBuildPackage;
    private MqttProperties mqtt;
    private JwtDeviceProperties jwtDeviceProperties;
}
