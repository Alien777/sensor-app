package pl.lasota.sensor.payload.to.dependet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.lasota.sensor.payload.Parse;

/**
 * Contract describing configure analog pin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PwmConfig implements Parse<PwmConfig, String> {

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


    @Override
    public String convert() {

        return pin+";"+freq+";"+resolution;
    }

    public PwmConfig revertConvert(String data) {
        String[] buffer = data.split(";");
        this.pin = Integer.parseInt(buffer[0]);
        this.freq = Integer.parseInt(buffer[1]);
        this.resolution = Integer.parseInt(buffer[2]);
        return this;
    }


    public static int length() {
        return 3;
    }
}
