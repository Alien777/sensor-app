package pl.lasota.sensor.bus.broadcast.impl;

import pl.lasota.sensor.bus.broadcast.BroadcastEvent;
import pl.lasota.sensor.bus.conventer.OnlyTakeConverter;
import pl.lasota.sensor.flow.model.FlowSensorI;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiConsumer;

public class FlowSensorIBroadcasterStream extends BroadcastEvent<FlowSensorI, FlowSensorI, String> {

    public FlowSensorIBroadcasterStream(Queue<BiConsumer<String, FlowSensorI>> consumers) throws Exception {
        super(new ArrayBlockingQueue<>(1000), consumers, OnlyTakeConverter::new, null);
    }
}
