package pl.lasota.sensor.flows.nodes.utils;

import org.springframework.stereotype.Service;
import pl.lasota.sensor.core.models.sensor.Sensor;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SensorListeningManager {
    private final ConcurrentHashMap<KeySensor, SensorListening> client = new ConcurrentHashMap<>();

    public void removeClient(KeySensor id) {
        client.remove(id);
    }

    public void addClient(KeySensor id, SensorListening sensorListening) {
        client.put(id, sensorListening);
    }

    public void broadcast(Sensor sensor) {
        client.forEach((keySensor, sensorListening) -> {
            if (keySensor.getDeviceId().equals(sensor.getDevice().getId())) {
                try {
                    sensorListening.onReceiving(sensor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
