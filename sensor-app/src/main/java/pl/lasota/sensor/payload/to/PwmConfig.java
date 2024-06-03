package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Contract describing configure analog pin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PwmConfig {

    /**
     * @hidden
     */
    public PwmConfig() {
    }

    @JsonProperty("pin")
    public int pin;

    @JsonProperty("freq")
    public int freq;

    @JsonProperty("resolution")
    public int resolution;

}
