package pl.lasota.sensor.api.filter;

import lombok.Data;
import pl.lasota.sensor.core.models.sensor.Sensor;

@Data
public class Context {
    private boolean shouldSendConfig = false;
    private Sensor sensor;
}
