package pl.lasota.sensor.exceptions;

public class SensorMemberException extends SensorException {
    public SensorMemberException() {
        super();
    }

    public SensorMemberException(String message) {
        super(message);
    }

    public SensorMemberException(Throwable e) {
        super(e, null);
    }

    public SensorMemberException(Throwable e, String message) {
        super(e, message);
    }

    public SensorMemberException(Throwable e, String message, Object... objects) {
        super(e, message, objects);
    }

    public SensorMemberException(String message, Object... objects) {
        super(message, objects);
    }
}