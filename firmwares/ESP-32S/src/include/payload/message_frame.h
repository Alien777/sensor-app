#ifndef MESSAGE_FRAME_H
#define MESSAGE_FRAME_H

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>
#include "sensor_structure.h"
#define MAX_VERSION_FIRMWARE_LENGTH 12
#define MAX_DEVICE_ID_LENGTH 12
#define MAX_MEMBER_ID_LENGTH 16
#define MAX_TOKEN_LENGTH 36
#define MAX_REQUEST_ID_LENGTH 36
#define MAX_PAYLOAD_LENGTH 256

typedef struct
{
    int configIdentifier;
    char versionFirmware[MAX_VERSION_FIRMWARE_LENGTH + 1];
    char deviceId[MAX_DEVICE_ID_LENGTH + 1];
    char memberId[MAX_MEMBER_ID_LENGTH + 1];
    char token[MAX_TOKEN_LENGTH + 1];
    char requestId[MAX_REQUEST_ID_LENGTH + 1];
    MessageType messageType;
    char payload[MAX_PAYLOAD_LENGTH];
    size_t payload_length;
} MessageFrame;

void revertConvert(MessageFrame *frame, const char *source);
void printMessageFrame(const MessageFrame *frame);
void convert(const MessageFrame *frame, char *result);
void createMessageFrame(MessageFrame *frame, long configId, const char *version, const char *deviceId,
                        const char *memberId, MessageType messageType, const char *token, const char *requestId, const char *payload);
void convertToInternal(Message *i, MessageFrame *mf);
#endif // MESSAGE_FRAME_H
