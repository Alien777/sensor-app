package pl.lasota.sensor.flows.properties;

import com.netflix.discovery.EurekaClient;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import pl.lasota.sensor.internal.apis.properties.InternalApisProperties;

@Configuration
@ConfigurationProperties("sensor.flows")
@Data
@Component
@NoArgsConstructor
public class FlowsProperties {
    private InternalApisProperties internalApisProperties;
    private EurekaClient eurekaClient;
    private String[] scanNodes;

    @Autowired
    public FlowsProperties(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public String getInstanceId() {
        return eurekaClient.getApplicationInfoManager().getInfo().getInstanceId();
    }
}
