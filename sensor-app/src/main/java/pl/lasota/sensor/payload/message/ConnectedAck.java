package pl.lasota.sensor.payload.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.PayloadParser;

@Data
@AllArgsConstructor(staticName = "of")
public class ConnectedAck implements PayloadParser<ConnectedAck, String> {

    public static ConnectedAck of(String source) {
        return new ConnectedAck();
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public ConnectedAck revertConvert(String source) {
        return of(source);
    }
}
