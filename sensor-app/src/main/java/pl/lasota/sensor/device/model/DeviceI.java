package pl.lasota.sensor.device.model;

import java.io.Serializable;
import java.util.UUID;

public record DeviceI(String id, String currentVersion, String name, boolean hasConfig, UUID token) implements Serializable {
}
