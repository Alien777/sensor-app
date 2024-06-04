package pl.lasota.sensor.bus.conventer;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;


public class OnlyTakeConverter<RESULT> implements Converter<RESULT, BlockingQueue<RESULT>> {

    @Override
    public void onConverter(BlockingQueue<RESULT> is, Consumer<RESULT> consumer) throws Exception {
        RESULT o = is.take();
        consumer.accept(o);
    }
}

