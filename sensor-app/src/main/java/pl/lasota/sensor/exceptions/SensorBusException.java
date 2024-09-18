package pl.lasota.sensor.exceptions;

public class SensorBusException extends SensorException {
    public SensorBusException() {
        super();
    }

    public SensorBusException(String message) {
        super(message);
    }

    public SensorBusException(Throwable e) {
        super(e, null);
    }

    public SensorBusException(Throwable e, String message) {
        super(e, message);
    }

    public SensorBusException(Throwable e, String message, Object... objects) {
        super(e, message, objects);
    }

    public SensorBusException(String message, Object... objects) {
        super(message, objects);
    }
}