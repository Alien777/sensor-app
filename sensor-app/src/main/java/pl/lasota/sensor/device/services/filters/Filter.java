package pl.lasota.sensor.device.services.filters;

public interface Filter<T, R> {
    void execute(T request, FilterContext context, Chain<R> chain) throws Exception;


    default void postExecute(T request, FilterContext context) throws Exception {
    }
}
