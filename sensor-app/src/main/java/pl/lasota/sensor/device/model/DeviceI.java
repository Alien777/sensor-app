package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record DeviceI(String id, String currentVersion, String name, boolean hasConfig, String token) implements Serializable {
}
