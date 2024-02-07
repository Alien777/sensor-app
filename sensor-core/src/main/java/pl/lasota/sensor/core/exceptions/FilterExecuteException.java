package pl.lasota.sensor.core.exceptions;

public class FilterExecuteException extends SensorException {

    public FilterExecuteException(String details, Throwable e) {
        super(e, details);
    }

    @Override
    public String getCode() {
        return "FE";
    }
}
