package pl.lasota.sensor.core.restapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.models.sensor.Sensor;

@FeignClient("sensor-flows")
public interface SensorFlowsEndpoint {
    String SENSOR_VALUE_ENDPOINT_PATH = "/api/flows/running";
    String STOP_ENDPOINT_PATH = "/api/flows/stop/{id}";

    @RequestMapping(method = RequestMethod.POST, value = "/api/flows/start/{id}")
    boolean startFlows(@PathVariable(value = "id") Long id, @RequestBody String config) throws Exception;

    @RequestMapping(method = RequestMethod.DELETE, value = STOP_ENDPOINT_PATH)
    boolean stopFlows(@PathVariable(value = "id") Long id) throws Exception;

    @RequestMapping(method = RequestMethod.PUT, value = SENSOR_VALUE_ENDPOINT_PATH)
    @Async
    void receivedSensorValue(@RequestBody Sensor sensor) throws Exception;

}
