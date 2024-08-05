#include "core/sensor_config.h"
const static char *TAG = "CONFIG_SENSOR_SETUP";
static void resetAllSettings();

void setup_config(Message *message)
{
    if (message->message_type != CONFIG)
    {
        ESP_LOGI(TAG, "This not CONFIG message");
        return;
    }
   
    resetAllSettings();

    setup_pwm(message);
    setup_digital(message);
    setup_analog(message);
}

static void resetAllSettings()
{

    reset_pwm_settings();
    reset_digital_settings();
    reset_analog_settings();

    ESP_LOGD(TAG, "Cleared all tasks");
}

