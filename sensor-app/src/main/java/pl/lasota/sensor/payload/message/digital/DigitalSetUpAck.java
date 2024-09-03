package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalSetUpAck implements PayloadParser<DigitalSetUpAck, String> {

    public static DigitalSetUpAck of(String source) {
        return new DigitalSetUpAck();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalSetUpAck revertConvert(String source) {
        return of(source);
    }
}
