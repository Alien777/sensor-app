package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadTearDown implements PayloadParser<AnalogReadTearDown, String> {

    public static AnalogReadTearDown of(String source) {
        return new AnalogReadTearDown();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadTearDown revertConvert(String source) {
        return of(source);
    }
}
