package pl.lasota.sensor.core.exceptions;

public class UserNotExistingException extends SensorException {
    @Override
    public String getCode() {
        return "UNE";
    }

}
