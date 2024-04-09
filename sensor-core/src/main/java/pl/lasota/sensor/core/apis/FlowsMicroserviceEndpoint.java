package pl.lasota.sensor.core.apis;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestClient;
import pl.lasota.sensor.core.apis.model.flow.FlowSaveT;
import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;
import pl.lasota.sensor.core.apis.model.flow.FlowStatusT;
import pl.lasota.sensor.core.apis.model.flow.FlowT;
import pl.lasota.sensor.core.apis.security.InternalClientInterceptorConfiguration;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@FeignClient(name = "sensor-flows", configuration = InternalClientInterceptorConfiguration.class)
@Primary
public interface FlowsMicroserviceEndpoint {

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
    String doesExecuteFlowId(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.PUT, value = SENSOR_VALUE_ENDPOINT_PATH)
    @Async
    void valueOfSensor(@RequestBody FlowSensorT sensor) throws Exception;


    default Optional<String> broadcastWhoExecuteFlow(Long flowId, DiscoveryClient dc, RestClient restClient, String currentServerId) {
        return getUrlOthersInstances(currentServerId, dc).
                stream()
                .map(uri -> restClient.get().uri(uri + WHERE_EXECUTE_ENDPOINT_PATH, flowId).retrieve().body(String.class))
                .filter(s -> s != null && !s.isBlank()).findFirst();
    }

    default FlowStatusT stopOnIndicateServer(Long flowId, String sendToServerId, DiscoveryClient dc, RestClient restClient) {
        Optional<URI> urlToInstance = getUrlToInstance(sendToServerId, dc);
        if (urlToInstance.isEmpty()) {
            return FlowStatusT.NOT_FOUND;
        }
        URI uri = urlToInstance.get();
        return restClient.delete().uri(uri + STOP_ENDPOINT_PATH, flowId).retrieve().body(FlowStatusT.class);
    }

    default void broadcastValueOfSensorToAllInstances(FlowSensorT sensor, DiscoveryClient dc, RestClient restClient) {
        getAllInstances(dc).forEach(uri ->
             restClient.put().uri(uri + SENSOR_VALUE_ENDPOINT_PATH).body(sensor).retrieve().toBodilessEntity());
    }

    private List<URI> getAllInstances(DiscoveryClient dc) {
        return dc.getInstances("sensor-flows")
                .stream()
                .map(ServiceInstance::getUri)
                .toList();
    }

    private Optional<URI> getUrlToInstance(String serverId, DiscoveryClient dc) {
        Optional<ServiceInstance> first = dc.getInstances("sensor-flows")
                .stream()
                .filter(instance -> instance.getInstanceId().equals(serverId))
                .findFirst();
        return first.map(ServiceInstance::getUri);
    }


    private List<URI> getUrlOthersInstances(String serverId, DiscoveryClient dc) {
        return dc.getInstances("sensor-flows")
                .stream()
                .filter(instance -> !instance.getInstanceId().equals(serverId))
                .map(ServiceInstance::getUri).toList();
    }
}
