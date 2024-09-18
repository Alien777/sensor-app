package pl.lasota.sensor.bus.conventer;

import pl.lasota.sensor.exceptions.SensorConvertException;

import java.util.function.Consumer;

public interface Converter<R, STREAM> extends AutoCloseable {

    void onConverter(STREAM is, Consumer<R> consumer) throws SensorConvertException;
}
