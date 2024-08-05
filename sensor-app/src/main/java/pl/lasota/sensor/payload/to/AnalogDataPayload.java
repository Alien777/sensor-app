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
public class AnalogDataPayload implements Parse<AnalogDataPayload, String> {

    /**
     * @hidden
     */
    public AnalogDataPayload() {
    }

    @JsonProperty("pin")
    private int pin;


    @Override
    public String convert() {

        return String.valueOf(pin);
    }

    @Override
    public AnalogDataPayload revertConvert(String source) {
        String[] split = source.split(";");
        this.pin = Integer.parseInt(split[0]);
        return this;
    }
}
