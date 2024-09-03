package pl.lasota.sensor.payload.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class ConfigAck implements PayloadParser<ConfigAck, String> {

    public static ConfigAck of(String source) {
        return new ConfigAck();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public ConfigAck revertConvert(String source) {
        return of(source);
    }
}
