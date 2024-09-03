package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record AnalogReadSetUpMessage(String deviceId, int gpio, int resolution) implements Serializable {
}
