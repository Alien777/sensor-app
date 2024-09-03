package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record PingMessage(String deviceId) implements Serializable {
}
