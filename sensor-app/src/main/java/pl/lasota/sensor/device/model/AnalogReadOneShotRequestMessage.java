package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record AnalogReadOneShotRequestMessage(String deviceId, int pin) implements Serializable {
}
