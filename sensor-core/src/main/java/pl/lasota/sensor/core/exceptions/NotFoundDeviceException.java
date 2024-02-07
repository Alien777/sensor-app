package pl.lasota.sensor.core.exceptions;

public class NotFoundDeviceException extends SensorException{

    @Override
    public String getCode() {
        return "NFD";
    }
}
