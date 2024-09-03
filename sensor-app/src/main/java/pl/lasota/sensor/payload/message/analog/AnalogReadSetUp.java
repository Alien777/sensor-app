package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadSetUp implements PayloadParser<AnalogReadSetUp, String> {

    private int gpio;

    private int width;

    public static AnalogReadSetUp of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int width = Integer.parseInt(buffer[1]);
        return new AnalogReadSetUp(gpio, width);
    }

    @Override
    public String convert() {
        return gpio + ";" + width;
    }

    @Override
    public AnalogReadSetUp revertConvert(String source) {
        return of(source);
    }

}
