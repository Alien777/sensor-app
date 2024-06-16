package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Contract describing configure analog pin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DigitalConfig {

    /**
     * @hidden
     */
    public DigitalConfig() {
    }

    @JsonProperty("pin")
    public int pin;
}
