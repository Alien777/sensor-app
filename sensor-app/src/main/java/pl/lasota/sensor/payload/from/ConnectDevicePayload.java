package pl.lasota.sensor.payload.from;

import pl.lasota.sensor.payload.MessageFrame;
import pl.lasota.sensor.payload.Parse;
import pl.lasota.sensor.entities.ConnectedDevice;
import pl.lasota.sensor.entities.Sensor;


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
