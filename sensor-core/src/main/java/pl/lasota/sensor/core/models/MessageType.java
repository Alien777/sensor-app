package pl.lasota.sensor.core.models;

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
     * When ESP send simple ADC value
     **/
    SINGLE_ADC_SIGNAL

}
