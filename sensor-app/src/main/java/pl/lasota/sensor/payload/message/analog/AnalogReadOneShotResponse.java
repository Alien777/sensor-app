package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadOneShotResponse implements PayloadParser<AnalogReadOneShotResponse, String> {

    private int gpio;
    private int value;

    public static AnalogReadOneShotResponse of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        int value = Integer.parseInt(buffer[1]);
        return new AnalogReadOneShotResponse(gpio, value);
    }

    @Override
    public String convert() {
        return gpio + ";" + value;
    }

    @Override
    public AnalogReadOneShotResponse revertConvert(String source) {
        return of(source);
    }
}
