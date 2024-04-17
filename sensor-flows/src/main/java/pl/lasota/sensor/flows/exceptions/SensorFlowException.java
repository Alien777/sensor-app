package pl.lasota.sensor.flows.exceptions;


import pl.lasota.sensor.core.exceptions.SensorException;

public class SensorFlowException extends SensorException {


    public SensorFlowException() {
        super();
    }

    public SensorFlowException(String message) {
        super(null, message);
    }

    public SensorFlowException(Throwable e) {
        super(e, null);
    }

    public SensorFlowException(Throwable e, String message) {
        super(e, message);
    }

    public SensorFlowException(Throwable e, String message, Object... objects) {
        super(e, message, objects);
    }

    public SensorFlowException(String message, Object... objects) {
        super(message, objects);
    }
}
