package pl.lasota.sensor.bus.conventer;

import java.io.*;
import java.util.function.Consumer;

public interface Converter<R, STREAM_OUT extends OutputStream, STREAM_IN extends InputStream> {

    void onConverter(STREAM_IN is, Consumer<R> consumer) throws Exception;

    default STREAM_OUT wrapperOutputStream(PipedOutputStream pipedOutputStream) throws IOException {
        return (STREAM_OUT) pipedOutputStream;
    }

    default STREAM_IN wrapperInputStream(PipedInputStream pipedInputStream) throws IOException
    {
        return (STREAM_IN) pipedInputStream;
    }

}
