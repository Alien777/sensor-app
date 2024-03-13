package pl.lasota.sensor.core.restapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.lasota.sensor.core.configs.CoreProperties;
import pl.lasota.sensor.core.models.sensor.Sensor;

import java.util.List;

import static pl.lasota.sensor.core.restapi.SensorFlowsEndpoint.SENSOR_VALUE_ENDPOINT_PATH;
import static pl.lasota.sensor.core.restapi.SensorFlowsEndpoint.STOP_ENDPOINT_PATH;

@RequiredArgsConstructor
@Slf4j
@Component
public class SensorFlowsHelper {

    private final DiscoveryClient discoveryClient;

    private final RestTemplate restTemplate;

    private final CoreProperties coreProperties;

    public void stopFlowsAllInstance(Long id) {
        List<ServiceInstance> instances = discoveryClient.getInstances(coreProperties.getSensorFlowsName());
        instances.parallelStream().map(instance -> instance.getUri().toString() +
                STOP_ENDPOINT_PATH
                        .replaceAll("\\{id}", id.toString())).forEach(url -> {
            try {
                restTemplate.delete(url);
            } catch (Exception e) {
                log.error("Occurred problem to send message to {}", url, e);
            }
        });
    }

    public void receivedSensorValueAllInstances(Sensor sensor) {
        List<ServiceInstance> instances = discoveryClient.getInstances(coreProperties.getSensorFlowsName());
        instances.parallelStream().map(instance -> instance.getUri().toString() + SENSOR_VALUE_ENDPOINT_PATH).forEach(url -> {
            try {
                restTemplate.put(url, sensor);
            } catch (Exception e) {
                log.error("Occurred problem to send message to {}", url, e);
            }
        });
    }
}
