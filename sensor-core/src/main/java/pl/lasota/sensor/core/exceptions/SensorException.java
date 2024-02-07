package pl.lasota.sensor.core.exceptions;

import lombok.Getter;

@Getter
public class SensorException extends Exception {

    private final String details;


    public SensorException() {
        this(null, null);
    }

    public SensorException(String details) {
        this(null, details);
    }

    public SensorException(Throwable e) {
        this(e, "");
    }

    public SensorException(Throwable e, String details) {
        super(details, e);
        this.details = details;
    }

    public String getCode() {
        return "SE";
    }

}
