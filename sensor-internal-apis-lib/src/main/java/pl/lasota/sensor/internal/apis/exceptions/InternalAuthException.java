package pl.lasota.sensor.internal.apis.exceptions;


import pl.lasota.sensor.core.exceptions.SensorException;

public class InternalAuthException extends SensorException {

    public InternalAuthException(String a, Throwable e) {
        super(e, a);
    }

    public InternalAuthException(String a) {
        super(a);
    }
}
