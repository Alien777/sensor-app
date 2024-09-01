package pl.lasota.sensor.payload.from;

import pl.lasota.sensor.entities.sensor.PwmAckDevice;
import pl.lasota.sensor.entities.sensor.Sensor;
import pl.lasota.sensor.payload.Parse;


/**
 * A model describing ping ack between device and Mqtt
 */
public class PingAckDevicePayload implements Parse<Sensor.SensorBuilder, String> {

    /**
     * @hidden
     */
    public PingAckDevicePayload() {
    }

    @Override
    public String convert() {
        return "";
    }

    @Override
    public Sensor.SensorBuilder revertConvert(String source) {
        return PwmAckDevice.builder();
    }
}
