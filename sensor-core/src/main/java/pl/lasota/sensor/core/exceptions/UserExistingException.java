package pl.lasota.sensor.core.exceptions;

public class UserExistingException extends SensorException {

    @Override
    public String getCode() {
        return "UE";
    }
}
