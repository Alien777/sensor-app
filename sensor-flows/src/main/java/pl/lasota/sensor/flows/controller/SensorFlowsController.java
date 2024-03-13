package pl.lasota.sensor.flows.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.restapi.SensorFlowsEndpoint;
import pl.lasota.sensor.flows.ManagerFlows;

@RestController
@RequiredArgsConstructor
public class SensorFlowsController implements SensorFlowsEndpoint {

    private final ManagerFlows managerFlows;

    @Override
    public boolean startFlows(Long id, String config) throws Exception {
        return managerFlows.start(id, config);
    }

    @Override
    public boolean stopFlows(Long id) throws Exception {
        return managerFlows.stop(id);
    }

    @Override
    public void receivedSensorValue(Sensor sensor) throws Exception {
        managerFlows.broadcast(sensor);
    }
}
