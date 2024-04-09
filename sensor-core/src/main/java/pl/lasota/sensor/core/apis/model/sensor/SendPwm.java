package pl.lasota.sensor.core.apis.model.sensor;

public record SendPwm(String deviceId, String token, int pin, long value) {
}
