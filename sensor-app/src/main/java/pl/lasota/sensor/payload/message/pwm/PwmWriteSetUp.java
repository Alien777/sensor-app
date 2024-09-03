package pl.lasota.sensor.payload.message.pwm;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class PwmWriteSetUp implements PayloadParser<PwmWriteSetUp, String> {

    private final int gpio;
    private final int frequency;
    private final int resolution;
    private final int duty;

    public static PwmWriteSetUp of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int frequency = Integer.parseInt(buffer[1]);
        int resolution = Integer.parseInt(buffer[2]);
        int duty = Integer.parseInt(buffer[3]);
        return new PwmWriteSetUp(gpio, frequency, resolution, duty);
    }

    @Override
    public String convert() {
        return gpio + ";" + frequency + ";" + resolution + ";" + duty;
    }

    // Static method to handle reverting from a String to an object
    @Override
    public PwmWriteSetUp revertConvert(String data) {
        return of(data);
    }
}
