package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadTearDownAck  implements PayloadParser<AnalogReadTearDownAck, String> {

    public static AnalogReadTearDownAck of(String source) {
        return new AnalogReadTearDownAck();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public AnalogReadTearDownAck revertConvert(String source) {
        return of(source);
    }
}
