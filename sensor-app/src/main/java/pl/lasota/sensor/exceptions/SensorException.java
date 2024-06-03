package pl.lasota.sensor.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Slf4j
public class SensorException extends RuntimeException {

    public SensorException() {
        super();
    }

    public SensorException(String message) {
        super(message);
    }

    public SensorException(Throwable e) {
        super(e);
    }

    public SensorException(String details, Throwable e) {
        super(details, e);
    }

    public SensorException(Throwable e, String details, Object... objects) {
        super(parse(details, objects), e);

    }

    public SensorException(String details, Object... objects) {
        super(parse(details, objects));

    }

    private static String parse(String details, Object... objects) {
        if (details == null) {
            return "";
        }
        if (objects == null || objects.length == 0) {
            return details;
        }
        List<Object> list = Arrays.stream(objects).toList();
        for (Object o : list) {
            details = details.replaceFirst("\\{}", o == null ? "null" : o.toString());
        }
        return details;
    }

    public String getMessagesOnlyStackTrace(UUID uuid) {
        StringBuilder messageStackTrace = new StringBuilder();
        Throwable current = this;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && !message.isEmpty()) {
                messageStackTrace.append(message).append(" || ");
            }
            current = current.getCause();
        }
        return messageStackTrace.append("ERROR CODE: ").append(uuid).toString();
    }
}
