package pl.lasota.sensor.core.models.mqtt.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
public class PwmPayload {

    /**
     * @hidden
     */
    public PwmPayload() {
    }

    @JsonProperty("pin")
    private int pin;

    @JsonProperty("value")
    private long value;

}
