package pl.lasota.sensor.core.apis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import pl.lasota.sensor.core.apis.model.flow.FlowSaveT;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.apis.model.flow.FlowT;
import pl.lasota.sensor.core.apis.security.InternalClientInterceptorConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@FeignClient(name = "sensor-flows", configuration = InternalClientInterceptorConfiguration.class)
@Primary
public interface FlowsMicroserviceEndpoint {
    Logger logger = LoggerFactory.getLogger(FlowsMicroserviceEndpoint.class);
    String SENSOR_VALUE_ENDPOINT_PATH = "/api-internal/flow/running";
    String STOP_ENDPOINT_PATH = "/api-internal/flow/stop/{id}";
    String SAVE_ENDPOINT_PATH = "/api-internal/flow/save";
    String DELETE_ENDPOINT_PATH = "/api-internal/flow/delete/{id}";
    String WHERE_EXECUTE_ENDPOINT_PATH = "/api-internal/flow/execute/{id}";

    @RequestMapping(method = RequestMethod.POST, value = SAVE_ENDPOINT_PATH)
    FlowStatusT save(@RequestBody FlowSaveT flowSaveT) throws Exception;

    @RequestMapping(method = RequestMethod.POST, value = "/api-internal/flow/start/{id}")
    FlowStatusT start(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.DELETE, value = STOP_ENDPOINT_PATH)
    FlowStatusT stop(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.DELETE, value = DELETE_ENDPOINT_PATH)
    FlowStatusT delete(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.GET, value = "/api-internal/flow/{id}")
    FlowT get(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.GET, value = "/api-internal/flow")
    List<FlowT> get() throws Exception;

    @RequestMapping(method = RequestMethod.GET, value = WHERE_EXECUTE_ENDPOINT_PATH)
    String findInstanceWhoExecute(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.PUT, value = SENSOR_VALUE_ENDPOINT_PATH)
    @Async
    void sendSensorValue(@RequestBody FlowSensorT sensor) throws Exception;


    default FlowStatusT stop(Long flowId, String serverId, DiscoveryClient dc, RestTemplate restTemplate) {
        Optional<String> urlToInstance = getUrlToInstance(serverId, dc, STOP_ENDPOINT_PATH);
        if (urlToInstance.isEmpty()) {
            return FlowStatusT.NOT_FOUND;
        }

        ResponseEntity<FlowStatusT> response = restTemplate.exchange(
                urlToInstance.get(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                FlowStatusT.class,
                flowId);

        return response.getBody();
    }



    default List<String> findInstanceWhoExecute(Long flowId, String serverId, DiscoveryClient dc, RestTemplate restTemplate) {
        return getUrlOthersInstances(serverId, dc, WHERE_EXECUTE_ENDPOINT_PATH)
                .stream().map(s -> {
                    ResponseEntity<String> response = restTemplate.exchange(
                            s,
                            HttpMethod.GET,
                            HttpEntity.EMPTY,
                            String.class,
                            flowId);
                    return response.getBody();
                }).filter(s -> s != null && !s.isBlank()).collect(Collectors.toList());
    }


    default void sendSensorValue(FlowSensorT sensor, DiscoveryClient dc, RestTemplate restTemplate) {
        for (String s : dc.getServices()
                .stream()
                .filter(s -> s.contains("sensor-flows"))
                .toList()) {
            List<ServiceInstance> instances = dc.getInstances(s);
            instances.stream().map(instance -> instance.getUri().toString() + SENSOR_VALUE_ENDPOINT_PATH).forEach(url -> {
                try {
                    restTemplate.put(url, sensor);
                } catch (Exception e) {
                    logger.error("Occurred problem  with send data to flow {} to {}", sensor, url, e);
                }
            });
        }
    }

    private Optional<String> getUrlToInstance(String serverId, DiscoveryClient dc, String path) {
        Optional<ServiceInstance> first = dc.getInstances("sensor-flows")
                .stream()
                .filter(instance -> instance.getInstanceId().equals(serverId))
                .findFirst();
        return first.map(instance -> instance.getUri() + path);
    }


    private List<String> getUrlOthersInstances(String serverId, DiscoveryClient dc, String path) {
        return dc.getInstances("sensor-flows")
                .stream()
                .filter(instance -> !instance.getInstanceId().equals(serverId))
                .map(instance -> instance.getUri() + path).collect(Collectors.toList());
    }
}
