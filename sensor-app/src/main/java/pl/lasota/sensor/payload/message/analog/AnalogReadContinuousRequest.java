package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadContinuousRequest implements PayloadParser<AnalogReadContinuousRequest, String> {


    public static AnalogReadContinuousRequest of(String source) {
        return new AnalogReadContinuousRequest();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadContinuousRequest revertConvert(String source) {
        return of(source);
    }
}
