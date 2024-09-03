package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalTearDownAck implements PayloadParser<DigitalTearDownAck, String> {

    public static DigitalTearDownAck of(String source) {
        return new DigitalTearDownAck();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalTearDownAck revertConvert(String source) {
        return of(source);
    }
}
