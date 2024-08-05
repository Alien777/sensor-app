package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import pl.lasota.sensor.payload.Parse;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PingPayload implements Parse<PingPayload, String> {


    /**
     * @hidden
     */
    public PingPayload() {
    }


    @Override
    public String convert() {
        return "";
    }

    @Override
    public PingPayload revertConvert(String source) {
        return this;
    }
}
