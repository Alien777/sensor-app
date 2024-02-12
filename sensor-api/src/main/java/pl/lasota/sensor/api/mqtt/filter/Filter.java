package pl.lasota.sensor.api.mqtt.filter;

public interface Filter<T, R> {
    void execute(T request, Context context, Chain<R> chain) throws Exception;


    default void postExecute(T request, Context context) throws Exception {
    }
}
