package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalSetUp implements PayloadParser<DigitalSetUp, String> {

    private int gpio;
    private int mode;

    public static DigitalSetUp of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int mode = Integer.parseInt(buffer[1]);

        return new DigitalSetUp(gpio, mode);
    }

    @Override
    public String convert() {

        return gpio + ";" + mode;
    }

    public DigitalSetUp revertConvert(String data) {
        return of(data);
    }

}
