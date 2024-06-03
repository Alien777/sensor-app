package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record ConfigCreateI(String version, String config) implements Serializable {
}
