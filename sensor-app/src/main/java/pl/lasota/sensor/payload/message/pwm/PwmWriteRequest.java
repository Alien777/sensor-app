package pl.lasota.sensor.payload.message.pwm;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class PwmWriteRequest implements PayloadParser<PwmWriteRequest, String> {

    private final int gpio;
    private final long duty;
    private final long duration;

    public static PwmWriteRequest of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int duty = Integer.parseInt(buffer[1]);
        int duration = Integer.parseInt(buffer[2]);
        return PwmWriteRequest.of(gpio, duty, duration);
    }


    @Override
    public String convert() {
        return gpio + ";" + duty + ";" + duration;
    }

    @Override
    public PwmWriteRequest revertConvert(String source) {
        return of(source);
    }
}
