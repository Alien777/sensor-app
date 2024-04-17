package pl.lasota.sensor.api.payload;

import pl.lasota.sensor.api.entities.Sensor;


/**
 * @hidden
 */
public interface Parse {
    /**
     * @hidden
     */
    Sensor.SensorBuilder parse(MessageFrame messageFrame);
}
