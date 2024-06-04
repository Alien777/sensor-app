package pl.lasota.sensor.bus.conventer;

import java.util.function.Consumer;

public interface Converter<R, STREAM> {

    void onConverter(STREAM is, Consumer<R> consumer) throws Exception;
}
