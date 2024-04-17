package pl.lasota.sensor.flows.nodes.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.internal.apis.api.flows.FlowSensorI;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SensorListeningManager {
    private final ConcurrentHashMap<KeySensor, SensorListening> client = new ConcurrentHashMap<>();

    public void removeClient(KeySensor id) {
        client.remove(id);
    }

    public void addClient(KeySensor id, SensorListening sensorListening) {
        client.put(id, sensorListening);
    }

    public void broadcast(FlowSensorI sensor) {
        client.forEach((keySensor, sensorListening) -> {
            if (keySensor.getDeviceId().equals(sensor.getDeviceId())) {
                sensorListening.onReceiving(sensor);
            }
        });
    }

}
