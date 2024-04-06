package pl.lasota.sensor.flows.nodes.utils;

import pl.lasota.sensor.core.apis.model.flow.FlowSensorT;

public interface SensorListening {

    void onReceiving(FlowSensorT sensor);
}



