package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadContinuousResponse implements PayloadParser<AnalogReadContinuousResponse, String> {

    public static AnalogReadContinuousResponse of(String source) {
        return new AnalogReadContinuousResponse();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadContinuousResponse revertConvert(String source) {
        return this;
    }
}
