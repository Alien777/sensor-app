#include "payload/message_frame.h"
#include "payload/message_type.h"
#include "sensor_structure.h"
#include "payload/payload.h"

int AnalogConfig_length()
{
    return 3;
}

int PwmConfig_length()
{
    return 3;
}

int DigitalConfig_length()
{
    return 1;
}

void revertConvertAnalogConfig(AnalogConfig *config, const char *source)
{
    char *buffer = strdup(source);
    char *token = strtok(buffer, ";");
    config->pin = atoi(token);

    token = strtok(NULL, ";");
    config->width = atoi(token);

    token = strtok(NULL, ";");
    config->atten = atoi(token);

    free(buffer);
}

void revertConvertPwmConfig(PwmConfig *config, const char *source)
{
    char *buffer = strdup(source);

    char *token = strtok(buffer, ";");
    config->pin = atoi(token);

    token = strtok(NULL, ";");
    config->freq = atoi(token);

    token = strtok(NULL, ";");
    config->resolution = atoi(token);

    free(buffer);
}

void revertConvertDigitalConfig(DigitalConfig *config, const char *source)
{
    char *buffer = strdup(source);
    char *token = strtok(buffer, ";");
    config->pin = atoi(token);

    free(buffer);
}

void analogExpotPayload(Message *mes, const char *source)
{
    char *buffer = strdup(source);
    char *token = strtok(buffer, ";");
    mes->analog_read_data.pin = atoi(token);
    free(buffer);
}

void digitalPayload(Message *mes, const char *source)
{
    char *buffer = strdup(source);

    char *token = strtok(buffer, ";");
    mes->digital_setup.pin = atoi(token);

    token = strtok(NULL, ";");
    mes->digital_setup.value = atoi(token);

    free(buffer);
}

void pwmSetupPayload(Message *mes, const char *source)
{
    char *buffer = strdup(source);

    char *token = strtok(buffer, ";");
    mes->pwn_setup.pin = atoi(token);

    token = strtok(NULL, ";");
    mes->pwn_setup.duty = atoi(token);

    token = strtok(NULL, ";");
    mes->pwn_setup.duration = atoi(token);

    free(buffer);
}

void configPayload(Message *mes, const char *source)
{
    char *buffer = strdup(source);
    char *lines[256];
    int line_count = 0;

    char *token = strtok(buffer, ";");
    while (token != NULL)
    {
        lines[line_count++] = token;
        token = strtok(NULL, ";");
    }

    int analogReaderSize = atoi(lines[0]);
    int pwmConfigSize = atoi(lines[1]);
    int digitalConfigSize = atoi(lines[2]);

    int index = 3;

    for (int i = 0; i < analogReaderSize; i++)
    {
        char analogConfigData[256] = {0};
        for (int j = 0; j < AnalogConfig_length(); j++)
        {
            strcat(analogConfigData, lines[index++]);
            if (j < AnalogConfig_length() - 1)
            {
                strcat(analogConfigData, ";");
            }
        }
        revertConvertAnalogConfig(&mes->analog_configs[i], analogConfigData);
    }

    for (int i = 0; i < pwmConfigSize; i++)
    {
        char pwmConfigData[256] = {0};
        for (int j = 0; j < PwmConfig_length(); j++)
        {
            strcat(pwmConfigData, lines[index++]);
            if (j < PwmConfig_length() - 1)
            {
                strcat(pwmConfigData, ";");
            }
        }
        revertConvertPwmConfig(&mes->pwm_configs[i], pwmConfigData);
    }

    for (int i = 0; i < digitalConfigSize; i++)
    {
        char digitalConfigData[256] = {0};
        for (int j = 0; j < DigitalConfig_length(); j++)
        {
            strcat(digitalConfigData, lines[index++]);
            if (j < DigitalConfig_length() - 1)
            {
                strcat(digitalConfigData, ";");
            }
        }
        revertConvertDigitalConfig(&mes->digital_configs[i], digitalConfigData);
    }

    mes->analog_configs_size = analogReaderSize;
    mes->pwm_configs_size = pwmConfigSize;
    mes->digital_configs_size = digitalConfigSize;

    free(buffer);
}
