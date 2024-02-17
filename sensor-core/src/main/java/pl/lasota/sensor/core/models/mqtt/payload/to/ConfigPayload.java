package pl.lasota.sensor.core.models.mqtt.payload.to;

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

    @JsonProperty("analog_reader")
    private List<AnalogConfigReader> analogReader = new ArrayList<>();

}
