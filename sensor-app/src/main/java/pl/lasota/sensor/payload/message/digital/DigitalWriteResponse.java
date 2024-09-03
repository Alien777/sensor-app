package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalWriteResponse implements PayloadParser<DigitalWriteResponse, String> {


    public static DigitalWriteResponse of(String source) {
        return new DigitalWriteResponse();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public DigitalWriteResponse revertConvert(String source) {
        return of(source);
    }
}
