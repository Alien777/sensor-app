package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalReadContinuousRequest implements PayloadParser<DigitalReadContinuousRequest, String> {


    public static DigitalReadContinuousRequest of(String source) {
        return new DigitalReadContinuousRequest();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalReadContinuousRequest revertConvert(String source) {
        return of(source);
    }
}
