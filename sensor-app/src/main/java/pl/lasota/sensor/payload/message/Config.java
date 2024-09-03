package pl.lasota.sensor.payload.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class Config implements PayloadParser<Config, String> {

    public static Config of(String source) {
        return new Config();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public Config revertConvert(String source) {
        return of(source);
    }
}
