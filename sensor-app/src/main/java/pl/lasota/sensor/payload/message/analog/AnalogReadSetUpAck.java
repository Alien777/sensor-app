package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadSetUpAck implements PayloadParser<AnalogReadSetUpAck, String> {


    public static AnalogReadSetUpAck of(String source) {
        return new AnalogReadSetUpAck();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadSetUpAck revertConvert(String source) {
        return of(source);
    }
}
