package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalReadOneShotResponse implements PayloadParser<DigitalReadOneShotResponse, String> {


    public static DigitalReadOneShotResponse of(String source) {
        return new DigitalReadOneShotResponse();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalReadOneShotResponse revertConvert(String source) {
        return of(source);
    }
}
