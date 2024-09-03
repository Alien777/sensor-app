#ifndef MESSAGE_FRAME_H
#define MESSAGE_FRAME_H

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <esp_log.h>
#include "sensor_structure.h"
#define MAX_VERSION_FIRMWARE_LENGTH 12
#define MAX_DEVICE_ID_LENGTH 12
#define MAX_MEMBER_ID_LENGTH 16
#define MAX_TOKEN_LENGTH 36
#define MAX_REQUEST_ID_LENGTH 36
#define MAX_PAYLOAD_LENGTH 256
 
typedef struct
{
    char device_id[MAX_DEVICE_ID_LENGTH + 1];
    char member_id[MAX_MEMBER_ID_LENGTH + 1];
    char token[MAX_TOKEN_LENGTH + 1];
    char firmware[MAX_VERSION_FIRMWARE_LENGTH + 1];
    char request_id[MAX_REQUEST_ID_LENGTH + 1];
    MessageType messageType;
    char payload[MAX_PAYLOAD_LENGTH];
} MessageFrame;

void chars_to_message_frame(MessageFrame *frame, const char *source);
void print_message_frame(const MessageFrame *frame);
void message_frame_to_chars(const MessageFrame *frame, char *result);
void create_message_frame_by_field(MessageFrame *frame,  const char *version, const char *deviceId,
                        const char *memberId, MessageType messageType, const char *token, const char *requestId, const char *payload);
void convert_message_frame_to_internal_object(ParsedMessage *i, MessageFrame *mf);
#endif // MESSAGE_FRAME_H
