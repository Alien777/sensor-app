package pl.lasota.sensor.core.exceptions;

public class NotFoundPinException extends SensorException {

    @Override
    public String getCode() {
        return "NFP";
    }
}
