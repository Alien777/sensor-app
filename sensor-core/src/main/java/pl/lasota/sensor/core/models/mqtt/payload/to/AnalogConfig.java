package pl.lasota.sensor.core.models.mqtt.payload.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Contract describing configure analog pin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalogConfig {


    /**
     * @hidden
     */
    public AnalogConfig() {
    }

    @JsonProperty("pin")
    public int pin;

    @JsonProperty("width")
    public int width;

    @JsonProperty("atten")
    public Integer atten;

    @JsonProperty("max_adc")
    public Integer maxAdc;

    @JsonProperty("min_adc")
    public Integer minAdc;

    @JsonProperty("sampling")
    public Integer sampling;

}
