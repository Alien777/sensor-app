package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.lasota.sensor.bus.conventer.ByteToObjectConverter;
import pl.lasota.sensor.flow.model.FlowSensorI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@Service
@RequiredArgsConstructor
public class FlowSensorIInputStreamBus extends InputStreamBus<String, FlowSensorI> {

    private final InputStreamBus<String, FlowSensorI>.Broadcast<ObjectOutputStream, ObjectInputStream> broadcast;

    public FlowSensorIInputStreamBus() throws IOException {
        broadcast = new Broadcast<ObjectOutputStream, ObjectInputStream>(ByteToObjectConverter::new, null);
    }

    @Override
    public InputStreamBus<String, FlowSensorI>.Broadcast<ObjectOutputStream, ObjectInputStream> takeBroadcaster(String streamInformation) {
        return broadcast;
    }
}
