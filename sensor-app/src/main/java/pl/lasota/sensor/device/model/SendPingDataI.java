package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record SendPingDataI(String deviceId) implements Serializable {
}
