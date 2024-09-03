package pl.lasota.sensor.payload.message.pwm;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class PwmWriteTearDown implements PayloadParser<PwmWriteTearDown, String> {

    private final int gpio;

    public static PwmWriteTearDown of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        return new PwmWriteTearDown(gpio);
    }


    @Override
    public String convert() {
        return gpio + "";
    }

    @Override
    public PwmWriteTearDown revertConvert(String source) {
        return of(source);
    }

}
