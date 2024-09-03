package pl.lasota.sensor.payload.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class Ping implements PayloadParser<Ping, String> {

    public static Ping of(String source) {
        return new Ping();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public Ping revertConvert(String source) {
        return of(source);
    }
}
