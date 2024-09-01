package pl.lasota.sensor.payload.from;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.lasota.sensor.entities.sensor.AnalogAckSensor;
import pl.lasota.sensor.entities.sensor.Sensor;
import pl.lasota.sensor.payload.Parse;


/**
 * A model describing the available fields from device during reading analog value
 */
public class AnalogAckDevicePayload implements Parse<Sensor.SensorBuilder, String> {

    /**
     * @hidden
     */
    public AnalogAckDevicePayload() {
    }

    @JsonProperty("pin")
    public int pin;

    @JsonProperty("adcRaw")
    public double adcRaw;


    @Override
    public String convert() {
        return STR."\{pin};\{adcRaw}";
    }

    @Override
    public Sensor.SensorBuilder revertConvert(String source) {
        String[] split = source.split(";");
        return AnalogAckSensor.builder()
                .pin(Integer.parseInt(split[0]))
                .adcRaw(Double.parseDouble(split[1]));

    }
}
