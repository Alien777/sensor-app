#include "payload/message_type.h"

const char* message_type_to_string(MessageType messageType) {
    switch (messageType) {
        case CONNECTED_ACK: return "CONNECTED_ACK";

        case PING: return "PING";
        case PING_ACK: return "PING_ACK";

        case CONFIG: return "CONFIG";
        case CONFIG_ACK: return "CONFIG_ACK";

        case PWM_WRITE_SET_UP: return "PWM_WRITE_SET_UP";
        case PWM_WRITE_SET_UP_ACK: return "PWM_WRITE_SET_UP_ACK";
        case PWM_WRITE_TEAR_DOWN: return "PWM_WRITE_TEAR_DOWN";
        case PWM_WRITE_TEAR_DOWN_ACK: return "PWM_WRITE_TEAR_DOWN_ACK";
        case PWM_WRITE_REQUEST: return "PWM_WRITE_REQUEST";
        case PWM_WRITE_RESPONSE: return "PWM_WRITE_RESPONSE";

        case ANALOG_READ_SET_UP: return "ANALOG_READ_SET_UP";
        case ANALOG_READ_SET_UP_ACK: return "ANALOG_READ_SET_UP_ACK";
        case ANALOG_READ_TEAR_DOWN: return "ANALOG_READ_TEAR_DOWN";
        case ANALOG_READ_TEAR_DOWN_ACK: return "ANALOG_READ_TEAR_DOWN_ACK";
        case ANALOG_READ_ONE_SHOT_REQUEST: return "ANALOG_READ_ONE_SHOT_REQUEST";
        case ANALOG_READ_ONE_SHOT_RESPONSE: return "ANALOG_READ_ONE_SHOT_RESPONSE";
        case ANALOG_READ_CONTINOUS_REQUEST: return "ANALOG_READ_CONTINOUS_REQUEST";
        case ANALOG_READ_CONTINOUS_RESPONSE: return "ANALOG_READ_CONTINOUS_RESPONSE";

        case DIGITAL_SET_UP: return "DIGITAL_SET_UP";
        case DIGITAL_SET_UP_ACK: return "DIGITAL_SET_UP_ACK";
        case DIGITAL_TEAR_DOWN: return "DIGITAL_TEAR_DOWN";
        case DIGITAL_TEAR_DOWN_ACK: return "DIGITAL_TEAR_DOWN_ACK";
        case DIGITAL_WRITE_REQUEST: return "DIGITAL_WRITE_REQUEST";
        case DIGITAL_WRITE_RESPONSE: return "DIGITAL_WRITE_RESPONSE";
        case DIGITAL_READ_ONE_SHOT_REQUEST: return "DIGITAL_READ_ONE_SHOT_REQUEST";
        case DIGITAL_READ_ONE_SHOT_RESPONSE: return "DIGITAL_READ_ONE_SHOT_RESPONSE";

        case DIGITAL_READ_CONTINOUS_REQUEST: return "DIGITAL_READ_CONTINOUS_REQUEST";
        case DIGITAL_READ_CONTINOUS_RESPONSE: return "DIGITAL_READ_CONTINOUS_RESPONSE";

        default: return "UNKNOWN";
    }
}

MessageType string_to_message_type(const char* messageType) {
    if (strcmp(messageType, "CONNECTED_ACK") == 0) return CONNECTED_ACK;

    if (strcmp(messageType, "PING") == 0) return PING;
    if (strcmp(messageType, "PING_ACK") == 0) return PING_ACK;

    if (strcmp(messageType, "CONFIG") == 0) return CONFIG;
    if (strcmp(messageType, "CONFIG_ACK") == 0) return CONFIG_ACK;

    if (strcmp(messageType, "PWM_WRITE_SET_UP") == 0) return PWM_WRITE_SET_UP;
    if (strcmp(messageType, "PWM_WRITE_SET_UP_ACK") == 0) return PWM_WRITE_SET_UP_ACK;
    if (strcmp(messageType, "PWM_WRITE_TEAR_DOWN") == 0) return PWM_WRITE_TEAR_DOWN;
    if (strcmp(messageType, "PWM_WRITE_TEAR_DOWN_ACK") == 0) return PWM_WRITE_TEAR_DOWN_ACK;
    if (strcmp(messageType, "PWM_WRITE_REQUEST") == 0) return PWM_WRITE_REQUEST;
    if (strcmp(messageType, "PWM_WRITE_RESPONSE") == 0) return PWM_WRITE_RESPONSE;

    if (strcmp(messageType, "ANALOG_READ_SET_UP") == 0) return ANALOG_READ_SET_UP;
    if (strcmp(messageType, "ANALOG_READ_SET_UP_ACK") == 0) return ANALOG_READ_SET_UP_ACK;
    if (strcmp(messageType, "ANALOG_READ_TEAR_DOWN") == 0) return ANALOG_READ_TEAR_DOWN;
    if (strcmp(messageType, "ANALOG_READ_TEAR_DOWN_ACK") == 0) return ANALOG_READ_TEAR_DOWN_ACK;
    if (strcmp(messageType, "ANALOG_READ_ONE_SHOT_REQUEST") == 0) return ANALOG_READ_ONE_SHOT_REQUEST;
    if (strcmp(messageType, "ANALOG_READ_ONE_SHOT_RESPONSE") == 0) return ANALOG_READ_ONE_SHOT_RESPONSE;
    if (strcmp(messageType, "ANALOG_READ_CONTINOUS_REQUEST") == 0) return ANALOG_READ_CONTINOUS_REQUEST;
    if (strcmp(messageType, "ANALOG_READ_CONTINOUS_RESPONSE") == 0) return ANALOG_READ_CONTINOUS_RESPONSE;

    if (strcmp(messageType, "DIGITAL_SET_UP") == 0) return DIGITAL_SET_UP;
    if (strcmp(messageType, "DIGITAL_SET_UP_ACK") == 0) return DIGITAL_SET_UP_ACK;
    if (strcmp(messageType, "DIGITAL_TEAR_DOWN") == 0) return DIGITAL_TEAR_DOWN;
    if (strcmp(messageType, "DIGITAL_TEAR_DOWN_ACK") == 0) return DIGITAL_TEAR_DOWN_ACK;
    if (strcmp(messageType, "DIGITAL_WRITE_REQUEST") == 0) return DIGITAL_WRITE_REQUEST;
    if (strcmp(messageType, "DIGITAL_WRITE_RESPONSE") == 0) return DIGITAL_WRITE_RESPONSE;
    if (strcmp(messageType, "DIGITAL_READ_ONE_SHOT_REQUEST") == 0) return DIGITAL_READ_ONE_SHOT_REQUEST;
    if (strcmp(messageType, "DIGITAL_READ_ONE_SHOT_RESPONSE") == 0) return DIGITAL_READ_ONE_SHOT_RESPONSE;

    if (strcmp(messageType, "DIGITAL_READ_CONTINOUS_REQUEST") == 0) return DIGITAL_READ_CONTINOUS_REQUEST;
    if (strcmp(messageType, "DIGITAL_READ_CONTINOUS_RESPONSE") == 0) return DIGITAL_READ_CONTINOUS_RESPONSE;

    return UNKNOWN;
}
