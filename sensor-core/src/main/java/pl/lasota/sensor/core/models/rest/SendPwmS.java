package pl.lasota.sensor.core.models.rest;

public record SendPwmS(String memberKey, String deviceKey, int pin, long value) {
}
