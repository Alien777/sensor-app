package pl.lasota.sensor.bus.broadcast;

import lombok.RequiredArgsConstructor;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

@RequiredArgsConstructor
public abstract class CustomerBroadcast<STREAM_INFORMATION, RESULT> {

    private final Queue<BiConsumer<STREAM_INFORMATION, RESULT>> customers = new ConcurrentLinkedQueue<>();

    public void addConsumer(BiConsumer<STREAM_INFORMATION, RESULT> consumer) {
        customers.add(consumer);
    }

    public void removeConsumer(BiConsumer<STREAM_INFORMATION, RESULT> consumer) {
        customers.remove(consumer);
    }

    public abstract Broadcast<?, ?, ?, ?> takeBroadcaster(STREAM_INFORMATION streamInformation) throws Exception;

    protected Queue<BiConsumer<STREAM_INFORMATION, RESULT>> getCustomers() {
        return customers;
    }
}
