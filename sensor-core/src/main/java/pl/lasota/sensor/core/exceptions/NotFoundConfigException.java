package pl.lasota.sensor.core.exceptions;


public class NotFoundConfigException extends SensorException {

    public NotFoundConfigException() {
        super();
    }

    public NotFoundConfigException(Throwable e) {
        super(e);
    }

    @Override
    public String getCode() {
        return "NFC";
    }
}
