package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.bus.broadcast.CustomerBroadcast;
import pl.lasota.sensor.bus.broadcast.impl.FlowSensorIBroadcasterStream;
import pl.lasota.sensor.flow.model.FlowSensorI;

@Service
@RequiredArgsConstructor
public class FlowSensorIInputStreamBus extends CustomerBroadcast<String, FlowSensorI> {

    private final FlowSensorIBroadcasterStream broadcast;

    public FlowSensorIInputStreamBus() throws Exception {
        broadcast = new FlowSensorIBroadcasterStream(super.getCustomers());
    }

    @Override
    public FlowSensorIBroadcasterStream takeBroadcaster(String s) {
        return broadcast;
    }
}
