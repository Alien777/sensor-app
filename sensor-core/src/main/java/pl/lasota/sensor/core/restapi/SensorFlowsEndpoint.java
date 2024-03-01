package pl.lasota.sensor.core.restapi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import pl.lasota.sensor.core.models.sensor.Sensor;

@FeignClient("sensor-flows")
public interface SensorFlowsEndpoint {
    String SENSOR_VALUE_ENDPOINT_PATH = "/api/flows/running";

    @RequestMapping(method = RequestMethod.POST, value = "/api/flows/start/{id}")
    void startFlows(@PathVariable(value = "id") Integer id) throws Exception;

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/flows/stop/{id}")
    void stopFlows(@PathVariable(value = "id") Integer id) throws Exception;

    @RequestMapping(method = RequestMethod.PUT, value = SENSOR_VALUE_ENDPOINT_PATH)
    @Async
    void sensorValue(@RequestBody Sensor sensor) throws Exception;

}
