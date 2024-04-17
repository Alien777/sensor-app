package pl.lasota.sensor.internal.apis.api.device;

import java.io.Serializable;

public record DeviceI(String id, String currentVersion, String name, boolean hasConfig, String token) implements Serializable {
}
