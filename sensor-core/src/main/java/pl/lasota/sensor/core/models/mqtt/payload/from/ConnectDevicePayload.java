package pl.lasota.sensor.core.models.mqtt.payload.from;

import pl.lasota.sensor.core.models.Parse;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.sensor.ConnectedDevice;
import pl.lasota.sensor.core.models.sensor.Sensor;

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
