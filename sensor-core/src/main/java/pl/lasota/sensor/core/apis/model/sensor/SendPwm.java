package pl.lasota.sensor.core.apis.model.sensor;

public record SendPwm(String deviceId, int pin, long value) {
}
