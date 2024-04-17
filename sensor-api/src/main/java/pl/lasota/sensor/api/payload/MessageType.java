package pl.lasota.sensor.api.payload;

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
     * During Host send pwm value to Device
     **/
    PWM;


    public static List<MessageType> getListMessageTypeFromDevice() {
        return List.of(DEVICE_CONNECTED, ANALOG);
    }
    public static boolean isFromDevice(MessageType messageType) {
        return getListMessageTypeFromDevice().contains(messageType);
    }
}
