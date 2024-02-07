package pl.lasota.sensor.core.exceptions;

import com.networknt.schema.ValidationMessage;

import java.util.Set;
import java.util.stream.Collectors;

public class ConfigParserException extends SensorException {

    public ConfigParserException(Set<ValidationMessage> message) {
        super(message.stream().map(ValidationMessage::getMessage).collect(Collectors.joining("\\n")));

    }

    public ConfigParserException(Throwable t) {
        super(t, t.getLocalizedMessage());
    }

    @Override
    public String getCode() {
        return "CPE";
    }
}
