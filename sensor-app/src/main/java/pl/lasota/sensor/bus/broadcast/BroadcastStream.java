package pl.lasota.sensor.bus.broadcast;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.conventer.Converter;

import java.io.*;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class BroadcastStream<RESULT, STREAM_INFORMATION, STREAM_OUT extends OutputStream, STREAM_IN extends InputStream> extends Broadcast<RESULT, byte[], STREAM_INFORMATION, STREAM_IN> implements AutoCloseable {

    private final STREAM_IN streamIn;
    private final STREAM_OUT streamOut;

    public BroadcastStream(Function<PipedOutputStream, STREAM_OUT> wrapperOutputStream,
                           Function<PipedInputStream, STREAM_IN> wrapperInputStream,
                           Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers,
                           Supplier<Converter<RESULT, STREAM_IN>> conventerSupplier,
                           STREAM_INFORMATION streamInformation) throws Exception {
        super(consumers, conventerSupplier, streamInformation);

        Converter<RESULT, STREAM_IN> converter = conventerSupplier.get();
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);

        streamOut = wrapperOutputStream.apply(pipedOutputStream);
        streamIn = wrapperInputStream.apply(pipedInputStream);
        run(converter, streamIn);
    }

    @Override
    public void write(byte[] o) throws IOException {
        streamOut.write(o);
    }

    @Override
    public void close() {
        try {
            streamOut.close();
        } catch (IOException e) {
            log.error("Problem with closing output stream", e);
        }
        try {
            streamIn.close();
        } catch (IOException e) {
            log.error("Problem with closing input stream", e);
        }
    }
}
