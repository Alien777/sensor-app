package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadOneShotResponse implements PayloadParser<AnalogReadOneShotResponse, String> {


    public static AnalogReadOneShotResponse of(String source) {
        return new AnalogReadOneShotResponse();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadOneShotResponse revertConvert(String source) {
        return of(source);
    }
}
