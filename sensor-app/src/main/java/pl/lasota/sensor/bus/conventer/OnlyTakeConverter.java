package pl.lasota.sensor.bus.conventer;

import pl.lasota.sensor.exceptions.SensorConvertException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class OnlyTakeConverter<RESULT> implements Converter<RESULT, BlockingQueue<RESULT>> {

    @Override
    public void onConverter(BlockingQueue<RESULT> is, Consumer<RESULT> consumer) throws SensorConvertException {
        RESULT o;
        try {
            o = is.poll(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (o != null) {
            consumer.accept(o);
        }

    }

    @Override
    public void close() throws Exception {}
}

