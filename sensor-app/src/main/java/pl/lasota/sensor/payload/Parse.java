package pl.lasota.sensor.payload;

import pl.lasota.sensor.entities.Sensor;


/**
 * @hidden
 */
public interface Parse {
    /**
     * @hidden
     */
    Sensor.SensorBuilder parse(MessageFrame messageFrame);
}
