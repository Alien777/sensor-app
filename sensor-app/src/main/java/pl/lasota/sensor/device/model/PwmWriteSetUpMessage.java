package pl.lasota.sensor.device.model;

import java.io.Serializable;

public record PwmWriteSetUpMessage(String deviceId,int gpio, int frequency, int resolution, int duty) implements Serializable {
}
