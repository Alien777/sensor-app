package pl.lasota.sensor.exceptions;


public class SensorFlowException extends SensorException {


    public SensorFlowException() {
        super();
    }

    public SensorFlowException(String message) {
        super( message);
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
