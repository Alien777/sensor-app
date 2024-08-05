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
public class DigitalConfig implements Parse<DigitalConfig, String> {

    /**
     * @hidden
     */
    public DigitalConfig() {
    }

    @JsonProperty("pin")
    public int pin;


    @Override
    public String convert() {

        return String.valueOf(pin);
    }

    public DigitalConfig revertConvert(String data) {
        String[] buffer = data.split(";");
        this.pin = Integer.parseInt(buffer[0]);
        return this;
    }


    public static int length() {
        return 1;
    }
}
