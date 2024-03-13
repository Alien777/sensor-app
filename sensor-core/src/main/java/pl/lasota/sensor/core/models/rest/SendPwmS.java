package pl.lasota.sensor.core.models.rest;

public record SendPwmS(String memberKey, String deviceId, int pin, long value) {
}
