#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <esp_log.h>
#include "payload/message_frame.h"
#include "payload/message_type.h"
#include "sensor_structure.h"
#include "payload/payload.h"
void convert(const MessageFrame *frame, char *result)
{
    snprintf(result, 512, "%s;%s;%s;%s;%i;%s;%s;%s",
             frame->deviceId, frame->memberId, frame->token, frame->versionFirmware,
             frame->configIdentifier, frame->requestId, message_type_to_string(frame->messageType), frame->payload);
}

void revertConvert(MessageFrame *frame, const char *source)
{

    char *buffer = strdup(source);
    char *field = strtok(buffer, ";");

    strncpy(frame->deviceId, field, sizeof(frame->deviceId));
    frame->deviceId[sizeof(frame->deviceId) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->memberId, field, sizeof(frame->memberId));
    frame->memberId[sizeof(frame->memberId) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->token, field, sizeof(frame->token));
    frame->token[sizeof(frame->token) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->versionFirmware, field, sizeof(frame->versionFirmware));
    frame->versionFirmware[sizeof(frame->versionFirmware) - 1] = '\0';

    field = strtok(NULL, ";");
    frame->configIdentifier = atol(field);

    field = strtok(NULL, ";");
    strncpy(frame->requestId, field, sizeof(frame->requestId));
    frame->requestId[sizeof(frame->requestId) - 1] = '\0';

    field = strtok(NULL, ";");
    frame->messageType = string_to_message_type(field);

    field = strtok(NULL, "");
    if (field)
    {
        strncpy(frame->payload, field, sizeof(frame->payload) - 1);
        frame->payload[sizeof(frame->payload) - 1] = '\0';
    }
    else
    {
        frame->payload[0] = '\0';
    }
    free(buffer);
}

void createMessageFrame(MessageFrame *frame, long configId, const char *version, const char *deviceId,
                        const char *memberId, MessageType messageType, const char *token, const char *requestId, const char *payload)
{
    frame->configIdentifier = configId;
    strncpy(frame->versionFirmware, version, sizeof(frame->versionFirmware) - 1);
    frame->versionFirmware[sizeof(frame->versionFirmware) - 1] = '\0';
    strncpy(frame->deviceId, deviceId, sizeof(frame->deviceId) - 1);
    frame->deviceId[sizeof(frame->deviceId) - 1] = '\0';
    strncpy(frame->memberId, memberId, sizeof(frame->memberId) - 1);
    frame->memberId[sizeof(frame->memberId) - 1] = '\0';
    frame->messageType = messageType;
    strncpy(frame->token, token, sizeof(frame->token) - 1);
    frame->token[sizeof(frame->token) - 1] = '\0';
    strncpy(frame->requestId, requestId, sizeof(frame->requestId) - 1);
    frame->requestId[sizeof(frame->requestId) - 1] = '\0';
    strncpy(frame->payload, payload, sizeof(frame->payload) - 1);
    frame->payload[sizeof(frame->payload) - 1] = '\0';
}

void convertToInternal(Message *i, MessageFrame *mf)
{
    i->config_id = mf->configIdentifier;
    i->request_id = mf->requestId;
    i->message_type = mf->messageType;

    if (mf->messageType == CONFIG)
    {
        configPayload(i, mf->payload);
    }

    if (mf->messageType == ANALOG)
    {
        analogExpotPayload(i, mf->payload);
    }

    if (mf->messageType == DIGITAL_WRITE)
    {
        digitalPayload(i, mf->payload);
    }

    if (mf->messageType == PWM)
    {
        pwmSetupPayload(i, mf->payload);
    }
}

void printMessageFrame(const MessageFrame *frame)
{
    printf("**************** %s ****************\n", message_type_to_string(frame->messageType));
    printf("configIdentifier: %i\n", frame->configIdentifier);
    printf("versionFirmware: %s\n", frame->versionFirmware);
    printf("deviceId: %s\n", frame->deviceId);
    printf("memberId: %s\n", frame->memberId);
    printf("token: %s\n", frame->token);
    printf("requestId: %s\n", frame->requestId);
    printf("messageType: %s\n", message_type_to_string(frame->messageType));
    printf("payload: %s\n", frame->payload);
    printf("**************** %s ****************\n", message_type_to_string(frame->messageType));
}
