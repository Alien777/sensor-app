package pl.lasota.sensor.device.model;

public record DigitalSetUpMessage(String deviceId, int gpio, int mode) {
}
