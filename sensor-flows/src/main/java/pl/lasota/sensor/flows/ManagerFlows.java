package pl.lasota.sensor.flows;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.flows.nodes.utils.SensorListeningManager;

@Service
@RequiredArgsConstructor
public class ManagerFlows {

    private final SensorListeningManager slm;

    public void listening(Sensor sensor) {
        slm.broadcast(sensor);
    }

    public void startFlow(Integer id) {

    }
}
