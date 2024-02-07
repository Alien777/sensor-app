package pl.lasota.sensor.core.exceptions;

public class NotFoundDeviceConfigException extends SensorException {
    public NotFoundDeviceConfigException() {
        super();
    }

    @Override
    public String getCode() {
        return "NFDC-1";
    }
}
