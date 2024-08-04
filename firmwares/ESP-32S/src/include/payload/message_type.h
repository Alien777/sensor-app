#ifndef MESSAGE_TYPE_H
#define MESSAGE_TYPE_H

#include <stdbool.h>
#include <string.h>
#include <stddef.h>
typedef enum
{
    DEVICE_CONNECTED = 1,
    CONFIG = 2,
    ANALOG = 3,
    ANALOG_ACK = 4,
    PWM = 5,
    PWM_ACK = 6,
    DIGITAL_WRITE = 7,
    PING = 8,
    PING_ACK = 9,
    UNKNOWN = -1
} MessageType;

const char *message_type_to_string(MessageType messageType);
MessageType string_to_message_type(const char *messageType);

#endif // MESSAGE_TYPE_H
