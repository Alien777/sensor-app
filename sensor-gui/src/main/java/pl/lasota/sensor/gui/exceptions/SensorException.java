package pl.lasota.sensor.gui.exceptions;

public class SensorException extends Exception {
    public SensorException(Throwable e) {
        super(e);
    }

    public SensorException(String e) {
        super(e);
    }

}
