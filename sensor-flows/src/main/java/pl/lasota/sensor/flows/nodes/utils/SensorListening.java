package pl.lasota.sensor.flows.nodes.utils;

import pl.lasota.sensor.internal.apis.api.flows.FlowSensorI;

public interface SensorListening {

    void onReceiving(FlowSensorI sensor);
}



