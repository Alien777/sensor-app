package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record SendDigitalI(String deviceId, int pin, int value) implements Serializable {
}
