package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record DeviceBuildI(String version, String name, String wifiSsid, String wifiPassword) implements Serializable {
}
