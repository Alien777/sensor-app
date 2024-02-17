package pl.lasota.sensor.core.models.mqtt.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Contract describing configure analog pin
 */
@Data
public class AnalogConfigReader {


    /**
     * @hidden
     */
    public AnalogConfigReader() {
    }

    @JsonProperty("pin")
    public int pin;

    @JsonProperty("atten")
    public int atten;

    @JsonProperty("width")
    public int width;

    @JsonProperty("max_adc")
    public int maxAdc;

    @JsonProperty("min_adc")
    public int minAdc;

    @JsonProperty("sampling")
    public int sampling;

}
