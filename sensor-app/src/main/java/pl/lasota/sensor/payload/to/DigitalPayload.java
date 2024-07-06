package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
@AllArgsConstructor
public class DigitalPayload {

    /**z
     * @hidden
     */
    public DigitalPayload() {
    }

    @JsonProperty("pin")
    private int pin;

    @JsonProperty("value")
    private long value;

}
