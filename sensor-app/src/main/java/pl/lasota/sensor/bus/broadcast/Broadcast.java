package pl.lasota.sensor.bus.broadcast;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.conventer.Converter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Slf4j
public abstract class Broadcast<RESULT, TYPE, STREAM_INFORMATION, STREAM> implements AutoCloseable {
    private final AtomicBoolean isStreamActive = new AtomicBoolean(true);

    protected final Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers;
    protected final Supplier<Converter<RESULT, STREAM>> conventerSupplier;
    protected final STREAM_INFORMATION streamInformation;

    public Broadcast(Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers, Supplier<Converter<RESULT, STREAM>> conventerSupplier, STREAM_INFORMATION streamInformation) throws IOException {
        this.consumers = consumers;
        this.conventerSupplier = conventerSupplier;
        this.streamInformation = streamInformation;
    }

    public abstract void write(TYPE type) throws Exception;

    protected void run(Converter<RESULT, STREAM> converter, STREAM streamIn) {
        new Thread(() -> {
            while (isStreamActive.get()) {
                try {
                    converter.onConverter(streamIn, result -> {
                        for (BiConsumer<STREAM_INFORMATION, RESULT> consumer : consumers) {
                            try {
                                consumer.accept(streamInformation, result);
                            } catch (Exception e) {
                                log.error("Problem with deal to consumer", e);
                            }
                        }
                    });
                } catch (Exception e) {
                    isStreamActive.set(false);
                    try {
                        close();
                    } catch (Exception ex) {
                        throw new RuntimeException("Problem with close stream", ex);
                    }
                }
            }
        }).start();
    }
}
