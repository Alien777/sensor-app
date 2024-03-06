package pl.lasota.sensor.flows.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.restapi.SensorFlowsEndpoint;
import pl.lasota.sensor.flows.ManagerFlows;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SensorFlowsController implements SensorFlowsEndpoint {

    private final ManagerFlows managerFlows;
    @Override
    public void startFlows(Integer id) throws Exception {
        managerFlows.startFlow(id);
    }

    @Override
    public void stopFlows(Integer id) throws Exception {

    }

    @Override
    public void sensorValue(Sensor sensor) throws Exception {
        managerFlows.listening(sensor);
    }
}
