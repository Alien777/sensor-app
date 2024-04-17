package pl.lasota.sensor.api.payload.from;

import pl.lasota.sensor.api.entities.ConnectedDevice;
import pl.lasota.sensor.api.payload.Parse;
import pl.lasota.sensor.api.entities.Sensor;
import pl.lasota.sensor.api.payload.MessageFrame;



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
