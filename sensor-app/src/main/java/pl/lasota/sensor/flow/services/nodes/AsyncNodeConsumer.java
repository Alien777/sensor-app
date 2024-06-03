package pl.lasota.sensor.flow.services.nodes;

import java.util.concurrent.*;
import java.util.function.BiConsumer;

public interface AsyncNodeConsumer<STREAM_INFORMATION, RESULT> extends BiConsumer<STREAM_INFORMATION, RESULT> {

    void consume(STREAM_INFORMATION streamInformation, RESULT result) throws Exception;

    default boolean preConsume(STREAM_INFORMATION streamInformation, RESULT result) {
        return true;
    }

    default void error(Exception e) {
        e.printStackTrace();
    }

    default ExecutorService getExecutorService() {
        return new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                Executors.defaultThreadFactory()
        );
    }

    default void accept(STREAM_INFORMATION streamInformation, RESULT result) {
        preConsume(streamInformation, result);
        getExecutorService().submit(() -> {
            try {
                consume(streamInformation, result);
            } catch (Exception e) {
                error(e);
            }
        });
    }

}
