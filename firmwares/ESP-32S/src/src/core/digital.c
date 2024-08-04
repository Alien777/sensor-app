#include "core/digital.h"

const static char *TAG = "DIGITAL";

static int digital_pin[100];

void setup_digital(Message *message)
{
    for (int i = 0; i < message->digital_configs_size; i++)
    {

        ESP_LOGE(TAG, "Config digital from pin %d %d", message->digital_configs[i].pin, i);

        gpio_config_t io_conf;
        io_conf.intr_type = GPIO_INTR_DISABLE;
        io_conf.mode = GPIO_MODE_OUTPUT;
        io_conf.pin_bit_mask = (1ULL << message->digital_configs[i].pin);
        io_conf.pull_down_en = GPIO_PULLDOWN_DISABLE;
        io_conf.pull_up_en = GPIO_PULLUP_DISABLE;
        gpio_config(&io_conf);
        digital_pin[i] = message->pwm_configs[i].pin;
    }
}
void reset_digital_settings()
{
    for (int i = 0; i < 100; i++)
    {
        if (digital_pin[i] != -1)
        {
            gpio_config_t io_conf_disable;
            io_conf_disable.intr_type = GPIO_INTR_DISABLE;
            io_conf_disable.mode = GPIO_MODE_DISABLE;
            io_conf_disable.pin_bit_mask = (1ULL << digital_pin[i]);
            io_conf_disable.pull_down_en = GPIO_PULLDOWN_DISABLE;
            io_conf_disable.pull_up_en = GPIO_PULLUP_DISABLE;
            gpio_config(&io_conf_disable);
        }
        digital_pin[i] = -1;
    }
}

void set_digital(Message *message)
{
    if (message->message_type != DIGITAL_WRITE)
    {
        ESP_LOGI(TAG, "This not DIGITAL_WRITE message");
        return;
    }
    ESP_LOGI("DIGITAL", "Setup value: %d for pin: %d", message->digital_setup.value, message->digital_setup.pin);
    gpio_set_level(message->digital_setup.pin, message->digital_setup.value);
}