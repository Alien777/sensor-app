package pl.lasota.sensor.payload.from;

import pl.lasota.sensor.entities.ConnectedDevice;
import pl.lasota.sensor.entities.Sensor;
import pl.lasota.sensor.payload.Parse;


/**
 * A model describing connection between device and Mqtt
 */
public class ConnectDevicePayload implements Parse<Sensor.SensorBuilder, String> {

    /**
     * @hidden
     */
    public ConnectDevicePayload() {
    }

    @Override
    public String convert() {
        return ";";
    }

    @Override
    public Sensor.SensorBuilder revertConvert(String source) {
        return ConnectedDevice.builder();

    }
}
