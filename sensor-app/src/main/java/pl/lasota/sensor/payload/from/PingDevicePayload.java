package pl.lasota.sensor.payload.from;

import pl.lasota.sensor.entities.PingDevice;
import pl.lasota.sensor.entities.Sensor;
import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.Parse;


/**
 * A model describing ping ack between device and Mqtt
 */
public class PingDevicePayload implements Parse {

    /**
     * @hidden
     */
    public PingDevicePayload() {
    }

    /**
     * @hidden
     */
    @Override
    public Sensor.SensorBuilder parse(MessageFrame messageFrame) {
        return PingDevice.builder();
    }
}
