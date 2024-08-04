package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.lasota.sensor.payload.Parse;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
@AllArgsConstructor
public class DigitalPayload implements Parse<DigitalPayload, String> {

    /**
     * z
     *
     * @hidden
     */
    public DigitalPayload() {
    }

    @JsonProperty("pin")
    private int pin;

    @JsonProperty("value")
    private long value;


    @Override
    public String convert() {

        return pin + ";" + value;
    }

    @Override
    public DigitalPayload revertConvert(String source) {
        String[] split = source.split(";");
        pin = Integer.parseInt(split[0]);
        value = Long.parseLong(split[1]);
        return this;
    }
}
