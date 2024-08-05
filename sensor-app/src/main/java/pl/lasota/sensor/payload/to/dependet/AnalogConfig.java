package pl.lasota.sensor.payload.to.dependet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import pl.lasota.sensor.payload.Parse;

import static pl.lasota.sensor.payload.Utils.integer;

/**
 * Contract describing configure analog pin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalogConfig implements Parse<AnalogConfig, String> {


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


    @Override
    public String convert() {
        return pin + ";" + width + ";" + atten;
    }

    @Override
    public AnalogConfig revertConvert(String source) {
        String[] buffer = source.split(";");
        this.pin = Integer.parseInt(buffer[0]);
        this.width = Integer.parseInt(buffer[1]);
        this.atten = integer(buffer[2]);
        return this;
    }


    public static int length() {
        return 3;
    }
}
