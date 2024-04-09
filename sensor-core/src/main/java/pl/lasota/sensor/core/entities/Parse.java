package pl.lasota.sensor.core.entities;

import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.entities.sensor.Sensor;


/**
 * @hidden
 */
public interface Parse {
    /**
     * @hidden
     */
    Sensor.SensorBuilder parse(MessageFrame messageFrame);
}
