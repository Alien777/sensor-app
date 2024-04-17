package pl.lasota.sensor.internal.apis.api.device;

import java.io.Serializable;

public record DeviceCreateI(String id, String name) implements Serializable {
}
