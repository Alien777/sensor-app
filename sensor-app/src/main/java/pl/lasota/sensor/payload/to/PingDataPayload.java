package pl.lasota.sensor.payload.to;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * A model describing the contract for sending data to the device to set the pwm value
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PingDataPayload {


    /**
     * @hidden
     */
    public PingDataPayload() {
    }


}
