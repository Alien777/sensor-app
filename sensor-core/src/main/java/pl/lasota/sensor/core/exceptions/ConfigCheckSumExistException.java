package pl.lasota.sensor.core.exceptions;

public class ConfigCheckSumExistException extends SensorException {
    public ConfigCheckSumExistException(String details) {
        super(details);
    }

    @Override
    public String getCode() {
        return "CCSE";
    }
}
