package pl.lasota.sensor.internal.apis.api.device;

import java.io.Serializable;

public record SendForAnalogDataI(String deviceId, int pin) implements Serializable {
}
