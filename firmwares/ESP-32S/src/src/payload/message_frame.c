#include "payload/message_frame.h"
#include "payload/message_type.h"
#include "sensor_structure.h"
#include "sensor_ntp.h"
#include "payload/payload.h"
void message_frame_to_chars(const MessageFrame *frame, char *result)
{
    snprintf(result, 512, "%s;%s;%s;%s;%i;%s;%s;%s",
             frame->device_id, frame->member_id, frame->token, frame->firmware,
             frame->config_id, frame->request_id, message_type_to_string(frame->messageType), frame->payload);
}

void chars_to_message_frame(MessageFrame *frame, const char *source)
{

    char *buffer = strdup(source);
    char *field = strtok(buffer, ";");

    strncpy(frame->device_id, field, sizeof(frame->device_id));
    frame->device_id[sizeof(frame->device_id) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->member_id, field, sizeof(frame->member_id));
    frame->member_id[sizeof(frame->member_id) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->token, field, sizeof(frame->token));
    frame->token[sizeof(frame->token) - 1] = '\0';

    field = strtok(NULL, ";");
    strncpy(frame->firmware, field, sizeof(frame->firmware));
    frame->firmware[sizeof(frame->firmware) - 1] = '\0';

    field = strtok(NULL, ";");
    frame->config_id = atol(field);

    field = strtok(NULL, ";");
    strncpy(frame->request_id, field, sizeof(frame->request_id));
    frame->request_id[sizeof(frame->request_id) - 1] = '\0';

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

void create_message_frame_by_field(MessageFrame *frame, long configId, const char *version, const char *deviceId,
                        const char *memberId, MessageType messageType, const char *token, const char *requestId, const char *payload)
{
    frame->config_id = configId;
    strncpy(frame->firmware, version, sizeof(frame->firmware) - 1);
    frame->firmware[sizeof(frame->firmware) - 1] = '\0';
    strncpy(frame->device_id, deviceId, sizeof(frame->device_id) - 1);
    frame->device_id[sizeof(frame->device_id) - 1] = '\0';
    strncpy(frame->member_id, memberId, sizeof(frame->member_id) - 1);
    frame->member_id[sizeof(frame->member_id) - 1] = '\0';
    frame->messageType = messageType;
    strncpy(frame->token, token, sizeof(frame->token) - 1);
    frame->token[sizeof(frame->token) - 1] = '\0';
    strncpy(frame->request_id, requestId, sizeof(frame->request_id) - 1);
    frame->request_id[sizeof(frame->request_id) - 1] = '\0';
    strncpy(frame->payload, payload, sizeof(frame->payload) - 1);
    frame->payload[sizeof(frame->payload) - 1] = '\0';
}

void convert_message_frame_to_internal_object(Message *i, MessageFrame *mf)
{
    i->config_id = mf->config_id;
    i->request_id = mf->request_id;
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

    if (mf->messageType == PWM_SETUP)
    {
        pwmSetupPayload(i, mf->payload);
    }
}

void print_message_frame(const MessageFrame *frame)
{
    printf("**************** %s ****************\n", message_type_to_string(frame->messageType));
    printf("configIdentifier: %i\n", frame->config_id);
    printf("versionFirmware: %s\n", frame->firmware);
    printf("deviceId: %s\n", frame->device_id);
    printf("memberId: %s\n", frame->member_id);
    printf("token: %s\n", frame->token);
    printf("requestId: %s\n", frame->request_id);
    printf("messageType: %s\n", message_type_to_string(frame->messageType));
    printf("payload: %s\n", frame->payload);
    printf("*****************END***************\n");
}
