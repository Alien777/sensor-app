package pl.lasota.sensor.core.models.mqtt.payload;

public enum MessageType {

    /**
     * When device ESP connected to mqtt
     **/
    DEVICE_CONNECTED,

    /**
     * When ESP received config
     **/
    CONFIG,

    /**
     * When ESP send simple Analog value
     **/
    SINGLE_ADC_SIGNAL,

    /**
     *  PWM message to esp
     **/
    PWM

}
