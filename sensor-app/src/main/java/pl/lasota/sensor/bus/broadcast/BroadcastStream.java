package pl.lasota.sensor.bus.broadcast;

import lombok.extern.slf4j.Slf4j;
import pl.lasota.sensor.bus.conventer.Converter;
import pl.lasota.sensor.exceptions.SensorBusException;

import java.io.*;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class BroadcastStream<RESULT, STREAM_INFORMATION, STREAM_OUT extends OutputStream, STREAM_IN extends InputStream> extends Broadcast<RESULT, byte[], STREAM_INFORMATION, STREAM_IN> implements AutoCloseable {

    private STREAM_IN streamIn;
    private STREAM_OUT streamOut;
    private final Function<PipedOutputStream, STREAM_OUT> wrapperOutputStream;
    private final Function<PipedInputStream, STREAM_IN> wrapperInputStream;

    public BroadcastStream(Function<PipedOutputStream, STREAM_OUT> wrapperOutputStream,
                           Function<PipedInputStream, STREAM_IN> wrapperInputStream,
                           Queue<BiConsumer<STREAM_INFORMATION, RESULT>> consumers,
                           Supplier<Converter<RESULT, STREAM_IN>> conventerSupplier,
                           STREAM_INFORMATION streamInformation) throws SensorBusException {
        super(consumers, conventerSupplier, streamInformation);
        this.wrapperOutputStream = wrapperOutputStream;
        this.wrapperInputStream = wrapperInputStream;

        Converter<RESULT, STREAM_IN> converter = conventerSupplier.get();
        super.run(converter, getNewStream());
    }


    @Override
    protected STREAM_IN getNewStream() {
        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream;
        try {
            pipedInputStream = new PipedInputStream(pipedOutputStream);
        } catch (IOException e) {
            throw new SensorBusException(e);
        }

        streamOut = wrapperOutputStream.apply(pipedOutputStream);
        streamIn = wrapperInputStream.apply(pipedInputStream);
        return streamIn;
    }

    @Override
    public void write(byte[] o) throws SensorBusException {
        try {
            blockIfError();
            streamOut.write(o);
        } catch (IOException e) {
            throw new SensorBusException(e);
        }
    }

    @Override
    public void close() {
        try {
            super.close();
        } catch (Exception e) {
            log.error("Problem with closing output stream", e);
        }
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
