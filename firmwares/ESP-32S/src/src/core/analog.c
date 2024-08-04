#include "core/analog.h"

#define MAX_TASKS 5

const static char *TAG = "ANALOG";

int taskCount = 0;

void request_for_analog_value(Message *message)
{
    if (message->message_type != ANALOG)
    {
        ESP_LOGI(TAG, "This not ANALOG message");
        return;
    }
    ESP_LOGI(TAG, "For pin: %d", message->analog_read_data.pin);

    int analogValue = adc1_get_raw(message->analog_read_data.pin);
    char response[100];
    snprintf(response, sizeof(response), "%d;%d", analogValue, message->analog_read_data.pin);
    publish(message->config_id, message->request_id, response, ANALOG_ACK);
}

void setup_analog(Message *message)
{
    for (int i = 0; i < message->analog_configs_size; i++)
    {

        ESP_LOGE(TAG, "Config analog from pin %d", message->analog_configs[i].pin);

        adc1_config_channel_atten(message->analog_configs[i].pin, message->analog_configs[i].atten);
        adc1_config_width(message->analog_configs[i].width);
        continue;
    }
}

void reset_analog_settings()
{
    for (int i = 0; i < taskCount; i++)
    {
    }
    taskCount = 0;
}
