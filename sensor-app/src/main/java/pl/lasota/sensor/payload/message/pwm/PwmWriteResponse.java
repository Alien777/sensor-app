package pl.lasota.sensor.payload.message.pwm;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class PwmWriteResponse implements PayloadParser<PwmWriteResponse, String> {

    public static PwmWriteResponse of(String source) {
        return new PwmWriteResponse();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public PwmWriteResponse revertConvert(String source) {
        return of(source);
    }
}
