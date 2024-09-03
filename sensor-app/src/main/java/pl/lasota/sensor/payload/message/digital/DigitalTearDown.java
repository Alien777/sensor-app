package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalTearDown implements PayloadParser<DigitalTearDown, String> {

    public static DigitalTearDown of(String source) {
        return new DigitalTearDown();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalTearDown revertConvert(String source) {
        return of(source);
    }
}
