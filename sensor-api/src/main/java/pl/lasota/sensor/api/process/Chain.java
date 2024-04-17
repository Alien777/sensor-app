package pl.lasota.sensor.api.process;

public interface Chain<T> {
    void doFilter(T request) throws Exception;
}
