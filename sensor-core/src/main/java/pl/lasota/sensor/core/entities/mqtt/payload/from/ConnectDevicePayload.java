package pl.lasota.sensor.core.entities.mqtt.payload.from;

import pl.lasota.sensor.core.entities.Parse;
import pl.lasota.sensor.core.entities.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.entities.sensor.ConnectedDevice;
import pl.lasota.sensor.core.entities.sensor.Sensor;

/**
 * A model describing connection between device and Mqtt
 */
public class ConnectDevicePayload implements Parse {

    /**
     * @hidden
     */
    public ConnectDevicePayload() {
    }

    /**
     * @hidden
     */
    @Override
    public Sensor.SensorBuilder parse(MessageFrame messageFrame) {
        return ConnectedDevice.builder();
    }
}
