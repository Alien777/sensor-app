package pl.lasota.sensor.payload;

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
    ANALOG,

    /**
     * Extort analog value
     **/
    ANALOG_EXTORT,

    /**
     *  Host send pwm value to Device
     **/
    PWM,

    /**
     * Answering on the pwm setup
     **/
    PWM_ACK,


    /**
     * During Host send digital value
     **/
    DIGITAL_WRITE,

    /**
     * Send ping to device
     **/
    PING,

    /**
     * Answering on the ping
     **/
    PING_ACK;


    public static List<MessageType> getListMessageTypeFromDevice() {
        return List.of(DEVICE_CONNECTED, ANALOG, PING_ACK, PWM_ACK);
    }

    public static boolean isFromDevice(MessageType messageType) {
        return getListMessageTypeFromDevice().contains(messageType);
    }
}
