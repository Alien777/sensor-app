package pl.lasota.sensor.bus.broadcast;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.conventer.Converter;
import pl.lasota.sensor.exceptions.SensorBusException;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public abstract class BroadcastEvent<RESULT, TYPE, STREAM_INFORMATION> extends Broadcast<RESULT, TYPE, STREAM_INFORMATION, BlockingQueue<TYPE>> implements AutoCloseable {

    private final BlockingQueue<TYPE> queue;

    public BroadcastEvent(BlockingQueue<TYPE> queue, Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers,
                          Supplier<Converter<RESULT, BlockingQueue<TYPE>>> conventerSupplier,
                          STREAM_INFORMATION streamInformation) throws IOException {
        super(consumers, conventerSupplier, streamInformation);
        this.queue = queue;

        Converter<RESULT, BlockingQueue<TYPE>> converter = conventerSupplier.get();
        super.run(converter, getNewStream());
    }

    @Override
    protected BlockingQueue<TYPE> getNewStream() {
        return queue;
    }

    @Override
    public void write(TYPE type) throws SensorBusException {
        if (type == null) {
            return;
        }
        boolean offer;
        blockIfError();
        try {
            offer = queue.offer(type, 100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SensorBusException(e);
        }
        if (!offer) {
            log.error("Queue overflow {}", type);
        }
    }
}
