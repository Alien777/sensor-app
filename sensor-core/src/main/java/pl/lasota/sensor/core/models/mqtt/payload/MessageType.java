package pl.lasota.sensor.core.models.mqtt.payload;

import java.util.List;

public enum MessageType {

    /**
     * During connected ESP to Mqtt
     **/
    DEVICE_CONNECTED,

    /**
     * During send config to Device
     **/
    CONFIG,

    /**
     * During received analog value from Device
     **/
    SINGLE_ADC_SIGNAL,

    /**
     * During Host send pwm value to Device
     **/
    PWM;


    public static boolean isFromEsp(MessageType messageType) {
        return List.of(DEVICE_CONNECTED, SINGLE_ADC_SIGNAL).contains(messageType);
    }
}
