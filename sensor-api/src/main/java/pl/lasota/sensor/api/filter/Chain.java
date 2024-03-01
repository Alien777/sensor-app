package pl.lasota.sensor.api.filter;

public interface Chain<T> {
    void doFilter(T request) throws Exception;
}
