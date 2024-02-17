package pl.lasota.sensor.core.models.mqtt.payload.from;

import pl.lasota.sensor.core.models.Parse;
import pl.lasota.sensor.core.models.mqtt.payload.MessageFrame;
import pl.lasota.sensor.core.models.sensor.Sensor;
import pl.lasota.sensor.core.models.sensor.SingleAdcSignal;

/**
 * A model describing the available fields from device during reading analog value
 */
public class AnalogValuePayload implements Parse {

    /**
     * @hidden
     */
    public AnalogValuePayload() {
    }

    /**
     * Raw analog value Double
     */
    private static final String ADC_RAW = "adc_raw";

    /**
     * Pin number Integer
     */
    private static final String PIN = "pin";

    /**
     * @hidden
     */
    @Override
    public Sensor.SensorBuilder parse(MessageFrame messageFrame) {
        double adcRaw = messageFrame.getPayload().findValue(ADC_RAW).asDouble();
        int pin = messageFrame.getPayload().findValue(PIN).asInt();

        return SingleAdcSignal.builder()
                .pin(pin)
                .adcRaw(adcRaw);
    }
}
