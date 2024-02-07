package pl.lasota.sensor.core.exceptions;


public class NotFoundSchemaConfigException extends SensorException {

    public NotFoundSchemaConfigException(Throwable e) {
        super(e);
    }

    @Override
    public String getCode() {
        return "NFSC";
    }
}
