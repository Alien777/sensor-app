package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record DigitalWriteMessage(String deviceId, int pin, boolean level) implements Serializable {
}
