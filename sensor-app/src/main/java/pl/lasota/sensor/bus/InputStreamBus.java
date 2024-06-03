package pl.lasota.sensor.bus;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.lasota.sensor.bus.conventer.Converter;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Supplier;


@RequiredArgsConstructor
public abstract class InputStreamBus<STREAM_INFORMATION, RESULT> {

    private static final int BUFFER_SIZE = 4096;
    private static final Logger log = LoggerFactory.getLogger(InputStreamBus.class);

    private final Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers = new ConcurrentLinkedQueue<>();

    public void addConsumer(BiConsumer<STREAM_INFORMATION, RESULT> consumer) {
        consumers.add(consumer);
    }

    public void removeConsumer(BiConsumer<STREAM_INFORMATION, RESULT> consumer) {
        consumers.remove(consumer);
    }

    public abstract Broadcast<?, ?> takeBroadcaster(STREAM_INFORMATION streamInformation) throws IOException;

    public class Broadcast<STREAM_OUT extends OutputStream, STREAM_IN extends InputStream> implements AutoCloseable {
        private final AtomicBoolean isStreamActive = new AtomicBoolean(true);
        private final Lock lock = new ReentrantLock();
        private final STREAM_INFORMATION streamInformation;
        private final STREAM_IN streamIn;
        public final STREAM_OUT streamOut;

        public Broadcast(Supplier<Converter<RESULT, STREAM_OUT, STREAM_IN>> conventerSupplier, STREAM_INFORMATION streamInformation) throws IOException {
            this.streamInformation = streamInformation;

            Converter<RESULT, STREAM_OUT, STREAM_IN> converter = conventerSupplier.get();
            PipedOutputStream pipedOutputStream = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream, BUFFER_SIZE);

            streamOut = converter.wrapperOutputStream(pipedOutputStream);
            streamIn = converter.wrapperInputStream(pipedInputStream);

            Thread run = run(converter);
            run.start();
        }

        public final void write(Synchro<STREAM_OUT> streamOutSynchro) {
            try {
                lock.lock();
                streamOutSynchro.synchro(streamOut);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void close() {
            isStreamActive.set(false);
            try {
                streamIn.close();
            } catch (IOException e) {
                log.error("problem with closing stream", e);
            }
            try {
                streamOut.close();
            } catch (IOException e) {
                log.error("problem with closing stream", e);
            }
        }

        private Thread run(Converter<RESULT, STREAM_OUT, STREAM_IN> converter) {
            return new Thread(() -> {
                while (isStreamActive.get()) {
                    try {
                        converter.onConverter(streamIn, s -> {
                            for (BiConsumer<STREAM_INFORMATION, RESULT> consumer : consumers) {
                                consumer.accept(streamInformation, s);
                            }
                        });
                    } catch (Exception e) {

                    }
                }
            });
        }
    }
}
