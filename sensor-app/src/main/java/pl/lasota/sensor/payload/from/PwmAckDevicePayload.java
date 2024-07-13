package pl.lasota.sensor.payload.from;

import pl.lasota.sensor.entities.PwmAckDevice;
import pl.lasota.sensor.entities.Sensor;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.Parse;


/**
 * A model describing ping ack between device and Mqtt
 */
public class PwmAckDevicePayload implements Parse {

    /**
     * @hidden
     */
    public PwmAckDevicePayload() {
    }

    /**
     * @hidden
     */
    @Override
    public Sensor.SensorBuilder parse(MessageFrame messageFrame) {
        return PwmAckDevice.builder();
    }
}
