package pl.lasota.sensor.flows.nodes.utils;

import pl.lasota.sensor.core.models.sensor.Sensor;

public interface SensorListening {

    void onReceiving(Sensor sensor);
}



