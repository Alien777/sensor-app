package pl.lasota.sensor.api.mqtt.filter;

public interface Chain<T> {
    void doFilter(T request);
}
