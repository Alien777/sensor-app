package pl.lasota.sensor.internal.apis.api.device;

import java.io.Serializable;

public record ConfigCreateI(String version, String config) implements Serializable {
}