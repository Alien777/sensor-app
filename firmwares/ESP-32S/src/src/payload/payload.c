#include "payload/message_frame.h"
#include "payload/message_type.h"
#include "sensor_structure.h"
#include "payload/payload.h"

void analogReadSetUpConvert(ParsedMessage *msg, const char *source)
{
    char *buffer = strdup(source);

    char *s = strtok(buffer, ";");
    msg->analog_read_set_up.gpio = atoi(s);

    s = strtok(NULL, ";");
    msg->analog_read_set_up.resolution = atoi(s);

    free(buffer);
}

void pwmWriteSetUpConvert(ParsedMessage *msg, const char *source)
{
    char *buffer = strdup(source);

    char *s = strtok(buffer, ";");
    msg->pwm_write_set_up.gpio = atoi(s);

    s = strtok(NULL, ";");
    msg->pwm_write_set_up.frequency = atoi(s);

    s = strtok(NULL, ";");
    msg->pwm_write_set_up.resolution = atoi(s);

    s = strtok(NULL, ";");
    msg->pwm_write_set_up.duty = atoi(s);

    free(buffer);
}

void digitalSetUpConvert(ParsedMessage *mes, const char *source)
{
    char *buffer = strdup(source);

    char *s = strtok(buffer, ";");
    mes->digital_set_up.gpio = atoi(s);

    s = strtok(NULL, ";");
    mes->digital_set_up.mode = atoi(s);

    free(buffer);
}

void analogReadOneShotRequestConvert(ParsedMessage *mes, const char *source)
{
    char *buffer = strdup(source);
    char *token = strtok(buffer, ";");
    mes->analog_read_one_shot_request.gpio = atoi(token);
    free(buffer);
}

void digitalWriteRequestConvert(ParsedMessage *mes, const char *source)
{
    char *buffer = strdup(source);

    char *s = strtok(buffer, ";");
    mes->digital_write_request.gpio = atoi(s);

    s = strtok(NULL, ";");
    mes->digital_write_request.level = atoi(s);

    free(buffer);
}

void pwmWriteRequestConvert(ParsedMessage *mes, const char *source)
{
    char *buffer = strdup(source);

    char *token = strtok(buffer, ";");
    mes->pwm_write_request.gpio = atoi(token);

    token = strtok(NULL, ";");
    mes->pwm_write_request.duty = atoi(token);

    token = strtok(NULL, ";");
    mes->pwm_write_request.duration = atoi(token);

    free(buffer);
}
