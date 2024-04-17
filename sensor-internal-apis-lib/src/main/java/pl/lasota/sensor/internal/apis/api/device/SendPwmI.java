package pl.lasota.sensor.internal.apis.api.device;

import java.io.Serializable;

public record SendPwmI(String deviceId, int pin, long value) implements Serializable {
}
