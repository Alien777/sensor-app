package pl.lasota.sensor.flow.services.nodes.utils;


import pl.lasota.sensor.flow.model.FlowSensorI;

public interface SensorListening {

    void onReceiving(FlowSensorI sensor);
}



