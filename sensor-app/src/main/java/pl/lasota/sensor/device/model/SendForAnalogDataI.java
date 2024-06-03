package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record SendForAnalogDataI(String deviceId, int pin) implements Serializable {
}
