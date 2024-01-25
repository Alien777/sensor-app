package pl.lasota.sensor.core.models.mqtt.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputPayload {

    @JsonProperty("pin")
    public int pin;

    @JsonProperty("type")
    public TypePayload type;

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
