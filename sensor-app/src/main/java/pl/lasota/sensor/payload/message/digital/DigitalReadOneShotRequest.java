package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalReadOneShotRequest implements PayloadParser<DigitalReadOneShotRequest, String> {

    public static DigitalReadOneShotRequest of(String source) {
        return new DigitalReadOneShotRequest();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalReadOneShotRequest revertConvert(String source) {
        return of(source);
    }
}
