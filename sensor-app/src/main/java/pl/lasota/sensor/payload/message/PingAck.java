package pl.lasota.sensor.payload.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class PingAck implements PayloadParser<PingAck, String> {

    public static PingAck of(String source) {
        return new PingAck();
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public PingAck revertConvert(String source) {
        return of(source);
    }
}
