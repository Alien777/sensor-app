package pl.lasota.sensor.api.process;

import lombok.Data;
import pl.lasota.sensor.api.entities.Sensor;


@Data
public class FilterContext {
    private boolean shouldSendConfig = false;
    private Sensor sensor;
}
