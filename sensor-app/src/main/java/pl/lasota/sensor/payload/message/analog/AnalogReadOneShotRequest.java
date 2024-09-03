package pl.lasota.sensor.payload.message.analog;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class AnalogReadOneShotRequest implements PayloadParser<AnalogReadOneShotRequest, String> {

    private final int gpio;

    public static AnalogReadOneShotRequest of(String source) {
        String[] buffer = source.split(";");
        int gpio = Integer.parseInt(buffer[0]);
        return AnalogReadOneShotRequest.of(gpio);
    }

    @Override
    public String convert() {
        return gpio + "";
    }

    @Override
    public AnalogReadOneShotRequest revertConvert(String source) {
        return of(source);
    }
}
