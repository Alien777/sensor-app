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
public class PwmPayload implements Parse<PwmPayload, String> {

    /**
     * z
     *
     * @hidden
     */
    public PwmPayload() {
    }

    @JsonProperty("pin")
    private int pin;

    @JsonProperty("duty")
    private long duty;

    @JsonProperty("duration")
    private long duration;


    @Override
    public String convert() {

        return pin + ";" + duty + ";" + duration;
    }

    @Override
    public PwmPayload revertConvert(String source) {
        String[] buffer = source.split(";");
        this.pin = Integer.parseInt(buffer[0]);
        this.duty = Long.parseLong(buffer[1]);
        this.duration = Long.parseLong(buffer[2]);
        return this;
    }


}
