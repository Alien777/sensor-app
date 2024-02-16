package pl.lasota.sensor.core.exceptions;

import pl.lasota.sensor.core.exceptions.SensorException;

public class NotSendCurrentConfigToDeviceException extends SensorException {
    @Override
    public String getCode() {
        return "NSCCTD";
    }
}
