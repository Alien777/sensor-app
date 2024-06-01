package pl.lasota.sensor.exceptions;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class SensorException extends RuntimeException {

    public static final String ID = " ID: ";
    private String details;

    public SensorException() {
        super(UUID.randomUUID().toString());
        parse(null);
    }

    public SensorException(String details) {
        super(UUID.randomUUID().toString());
        parse(details);
    }

    public SensorException(Throwable e) {
        super(UUID.randomUUID().toString(), e);
        parse(null);
    }

    public SensorException(Throwable e, String details) {
        super(UUID.randomUUID().toString(), e);
        parse(details);
    }

    public SensorException(Throwable e, String details, Object... objects) {
        super(UUID.randomUUID().toString(), e);
        parse(details, objects);
    }

    public SensorException(String details, Object... objects) {
        super(UUID.randomUUID().toString());
        parse(details, objects);
    }

    private void parse(String details, Object... objects) {
        if (details == null) {
            this.details = ID + super.getMessage();
            return;
        }
        if (objects == null || objects.length == 0) {
            this.details = details + ID + super.getMessage();
            return;
        }
        List<Object> list = Arrays.stream(objects).toList();
        for (Object o : list) {
            details = details.replaceFirst("\\{}", o == null ? "null" : o.toString());
        }
        this.details = details + ID + super.getMessage();
    }
}
