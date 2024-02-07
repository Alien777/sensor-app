package pl.lasota.sensor.core.exceptions;


public class NotFoundDefaultConfigException extends SensorException {

    public NotFoundDefaultConfigException() {
        super();
    }

    public NotFoundDefaultConfigException(Throwable e) {
        super(e);
    }

    @Override
    public String getCode() {
        return "NFDC";
    }
}
