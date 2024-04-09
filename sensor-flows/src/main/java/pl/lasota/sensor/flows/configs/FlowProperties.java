package pl.lasota.sensor.flows.configs;

import com.netflix.discovery.EurekaClient;
import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class FlowProperties {
    private final EurekaClient eurekaClient;

    public FlowProperties(EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    public String getInstanceId() {
        return eurekaClient.getApplicationInfoManager().getInfo().getInstanceId();
    }
}
