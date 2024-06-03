package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record DeviceCreateI(String id, String name) implements Serializable {
}
