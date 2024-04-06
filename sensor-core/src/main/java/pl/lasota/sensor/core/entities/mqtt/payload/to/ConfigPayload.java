package pl.lasota.sensor.core.entities.mqtt.payload.to;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Contract describing config payload
 */
@Data
public class ConfigPayload {

    /**
     * @hidden
     */
    public ConfigPayload() {
    }

    @JsonProperty("analog_configs")
    private List<AnalogConfig> analogReader = new ArrayList<>();

    @JsonProperty("pwm_configs")
    private List<PwmConfig> pwmConfig = new ArrayList<>();

}
