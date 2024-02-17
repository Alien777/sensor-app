package pl.lasota.sensor.core.models;

import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.sensor.Sensor;


/**
 * @hidden
 */
public interface Parse {
    /**
     * @hidden
     */
    Sensor.SensorBuilder parse(MessageFrame messageFrame);
}
