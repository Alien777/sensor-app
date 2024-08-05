#include "payload/message_type.h"

const char* message_type_to_string(MessageType messageType) {
    switch (messageType) {
        case DEVICE_CONNECTED: return "DEVICE_CONNECTED";
        case CONFIG: return "CONFIG";
        case ANALOG: return "ANALOG";
        case ANALOG_ACK: return "ANALOG_ACK";
        case PWM: return "PWM";
        case PWM_ACK: return "PWM_ACK";
        case DIGITAL_WRITE: return "DIGITAL_WRITE";
        case PING: return "PING";
        case PING_ACK: return "PING_ACK";
        default: return "UNKNOWN";
    }
}


  MessageType string_to_message_type(const char* messageType) {
    if (strcmp(messageType, "DEVICE_CONNECTED") == 0) return DEVICE_CONNECTED;
    if (strcmp(messageType, "CONFIG") == 0) return CONFIG;
    if (strcmp(messageType, "ANALOG") == 0) return ANALOG;
    if (strcmp(messageType, "ANALOG_ACK") == 0) return ANALOG_ACK;
    if (strcmp(messageType, "PWM") == 0) return PWM;
    if (strcmp(messageType, "PWM_ACK") == 0) return PWM_ACK;
    if (strcmp(messageType, "DIGITAL_WRITE") == 0) return DIGITAL_WRITE;
    if (strcmp(messageType, "PING") == 0) return PING;
    if (strcmp(messageType, "PING_ACK") == 0) return PING_ACK;
    return UNKNOWN;
}
