package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record PwmWriteRequestMessage(String deviceId, int pin, long value, long duration) implements Serializable {
}
