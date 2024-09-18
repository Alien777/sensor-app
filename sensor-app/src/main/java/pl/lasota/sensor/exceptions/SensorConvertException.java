package pl.lasota.sensor.exceptions;

public class SensorConvertException extends SensorException {
    public SensorConvertException() {
        super();
    }

    public SensorConvertException(String message) {
        super(message);
    }

    public SensorConvertException(Throwable e) {
        super(e, null);
    }

    public SensorConvertException(Throwable e, String message) {
        super(e, message);
    }

    public SensorConvertException(Throwable e, String message, Object... objects) {
        super(e, message, objects);
    }

    public SensorConvertException(String message, Object... objects) {
        super(message, objects);
    }
}