package pl.lasota.sensor.exceptions;


public class SensorApiException extends SensorException {
    public SensorApiException() {
        super();
    }

    public SensorApiException(String message) {
        super(message);
    }

    public SensorApiException(Throwable e) {
        super(e, null);
    }

    public SensorApiException(Throwable e, String message) {
        super(e, message);
    }

    public SensorApiException(Throwable e, String message, Object... objects) {
        super(e, message, objects);
    }

    public SensorApiException(String message, Object... objects) {
        super(message, objects);
    }
}
