package pl.lasota.sensor.core.entities.mqtt.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
@AllArgsConstructor
public class PwmPayload {

    /**z
     * @hidden
     */
    public PwmPayload() {
    }

    @JsonProperty("pin")
    private int pin;

    @JsonProperty("duty")
    private long duty;

}