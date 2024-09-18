package pl.lasota.sensor.bus.broadcast;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.conventer.Converter;
import pl.lasota.sensor.exceptions.SensorBusException;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public abstract class Broadcast<RESULT, TYPE, STREAM_INFORMATION, STREAM> implements AutoCloseable {
    private final AtomicBoolean isStreamActive = new AtomicBoolean(true);

    private final Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers;
    private final STREAM_INFORMATION streamInformation;
    private final Supplier<Converter<RESULT, STREAM>> conventerSupplier;

    protected Broadcast(Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers, Supplier<Converter<RESULT, STREAM>> conventerSupplier, STREAM_INFORMATION streamInformation) {
        this.consumers = consumers;
        this.conventerSupplier = conventerSupplier;
        this.streamInformation = streamInformation;
    }

    protected abstract void write(TYPE type) throws SensorBusException;

    protected abstract STREAM getNewStream();

    protected void blockIfError() {
        if (!isStreamActive.get()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log.info("Occurred error", e);
            }
        }
    }

    protected void run(Converter<RESULT, STREAM> converter, STREAM streamIn) {
        new Thread(() -> {
            try {
                while (isStreamActive.get()) {
                    converter.onConverter(streamIn, result -> {
                        for (BiConsumer<STREAM_INFORMATION, RESULT> consumer : consumers) {
                            try {
                                consumer.accept(streamInformation, result);
                            } catch (Exception e) {
                                log.error("Problem with deal to consumer", e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                try {
                    close();
                    STREAM newStream = getNewStream();
                    run(converter, newStream);
                } catch (Exception ex) {
                    throw new SensorBusException(ex, "Problem with close stream");
                } finally {
                    isStreamActive.set(true);
                }
            }
        }).start();

    }

    @Override
    public void close() throws Exception {
        isStreamActive.set(false);
        conventerSupplier.get().close();
    }
}
