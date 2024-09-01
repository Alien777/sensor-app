package pl.lasota.sensor.device.services.filters;

import lombok.Data;
import pl.lasota.sensor.entities.sensor.Sensor;


@Data
public class FilterContext {
    private boolean shouldSendConfig = false;
    private Sensor sensor;
}
