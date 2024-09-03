package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalReadContinuousResponse implements PayloadParser<DigitalReadContinuousResponse, String> {


    public static DigitalReadContinuousResponse of(String source) {
        return new DigitalReadContinuousResponse();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalReadContinuousResponse revertConvert(String source) {
        return of(source);
    }
}
