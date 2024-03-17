package pl.lasota.sensor.core.exceptions;


public class FlowRuntimeException extends RuntimeException {


    public FlowRuntimeException(Throwable e) {
        super(e);
    }

    public FlowRuntimeException(String e) {
        super(e);
    }
}
