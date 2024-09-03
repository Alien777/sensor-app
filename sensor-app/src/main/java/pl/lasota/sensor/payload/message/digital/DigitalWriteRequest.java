package pl.lasota.sensor.payload.message.digital;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class DigitalWriteRequest implements PayloadParser<DigitalWriteRequest, String> {

    private final int gpio;
    private final int level;

    public static DigitalWriteRequest of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int level = Integer.parseInt(buffer[1]);
        return new DigitalWriteRequest(gpio, level);
    }


    @Override
    public String convert() {
        return gpio + ";" + level;
    }

    @Override
    public DigitalWriteRequest revertConvert(String source) {
        return of(source);
    }
}
