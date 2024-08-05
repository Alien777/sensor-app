package pl.lasota.sensor.payload;

import lombok.Getter;

import java.util.List;

@Getter
public enum MessageType {

    /**
     * During connected ESP to Mqtt
     **/
    DEVICE_CONNECTED(1),

    /**
     * During send config to Device
     **/
    CONFIG(2),

    /**
     * During received analog value from Device
     **/
    ANALOG(3),

    /**
     * Extort analog value
     **/
    ANALOG_ACK(4),

    /**
     *  Host send pwm value to Device
     **/
    PWM(5),

    /**
     * Answering on the pwm setup
     **/
    PWM_ACK(6),


    /**
     * During Host send digital value
     **/
    DIGITAL_WRITE(7),

    /**
     * Send ping to device
     **/
    PING(8),

    /**
     * Answering on the ping
     **/
    PING_ACK(9);


    public static List<MessageType> getListMessageTypeFromDevice() {
        return List.of(DEVICE_CONNECTED, ANALOG, PING_ACK, PWM_ACK);
    }

    public static boolean isFromDevice(MessageType messageType) {
        return getListMessageTypeFromDevice().contains(messageType);
    }

    public static MessageType fromValue(int value) {
        for (MessageType type : values()) {
            if (type.value == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown MessageType value: " + value);
    }

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

}
