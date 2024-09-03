package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record ConfigMessage(String deviceId) implements Serializable {
}
