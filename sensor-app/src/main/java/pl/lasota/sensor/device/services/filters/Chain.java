package pl.lasota.sensor.device.services.filters;

public interface Chain<T> {
    void doFilter(T request) throws Exception;
}
