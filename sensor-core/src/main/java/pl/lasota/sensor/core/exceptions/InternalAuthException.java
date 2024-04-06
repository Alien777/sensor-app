package pl.lasota.sensor.core.exceptions;

public class InternalAuthException extends SensorException {

    public InternalAuthException(String a, Throwable e) {
        super(e, a);
    }

    public InternalAuthException(String a) {
        super(a);
    }
}
