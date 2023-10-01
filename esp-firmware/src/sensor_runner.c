#include "sensor_runner.h"

void lisening_on_port(Message message)
{

    if (message.message_type != CONFIG)
    {
        ESP_LOGI("TT", "NONE");
        return;
    }
    ESP_LOGI("TT", "OKKKKK");
}