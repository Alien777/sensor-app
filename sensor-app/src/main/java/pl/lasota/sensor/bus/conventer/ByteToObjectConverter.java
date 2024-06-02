package pl.lasota.sensor.bus.conventer;

import java.io.*;
import java.util.function.Consumer;


public class ByteToObjectConverter<RESULT> implements Converter<RESULT, ObjectOutputStream, ObjectInputStream> {

    @Override
    public void onConverter(ObjectInputStream in, Consumer<RESULT> consumer) throws Exception {
        Object o = in.readObject();
        if (o != null) {
            consumer.accept((RESULT) o);
        }
    }

    @Override
    public ObjectOutputStream wrapperOutputStream(PipedOutputStream pipedOutputStream) throws IOException {
        return new ObjectOutputStream(pipedOutputStream);
    }

    @Override
    public ObjectInputStream wrapperInputStream(PipedInputStream pipedOutputStream) throws IOException {
        return new ObjectInputStream(pipedOutputStream);
    }
}

