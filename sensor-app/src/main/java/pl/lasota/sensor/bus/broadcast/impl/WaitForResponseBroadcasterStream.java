package pl.lasota.sensor.bus.broadcast.impl;

import pl.lasota.sensor.bus.broadcast.BroadcastEvent;
import pl.lasota.sensor.bus.conventer.OnlyTakeConverter;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.function.BiConsumer;

public class WaitForResponseBroadcasterStream extends BroadcastEvent<String, String, String> {

    public WaitForResponseBroadcasterStream(Queue<BiConsumer<String, String>> consumers) throws Exception {
        super(new ArrayBlockingQueue<>(1000), consumers, OnlyTakeConverter::new, null);
    }
}
