package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record SendPwmI(String deviceId, int pin, long value) implements Serializable {
}
