#include "core/digital.h"

const static char *TAG = "DIGITAL";

static bool digital_pin[GPIO_NUM_MAX] = {[0 ... GPIO_NUM_MAX - 1] = false};

void digital_set_up(ParsedMessage message, DigitalSetUp payload)
{

    if (message.message_type != DIGITAL_SET_UP)
    {
        return;
    }

    ESP_LOGE(TAG, "Config digital from pin %d", payload.gpio);

    gpio_config_t io_conf;
    io_conf.intr_type = GPIO_INTR_DISABLE;
    io_conf.mode = GPIO_MODE_OUTPUT;
    io_conf.pin_bit_mask = (1ULL << payload.gpio);
    io_conf.pull_down_en = GPIO_PULLDOWN_DISABLE;
    io_conf.pull_up_en = GPIO_PULLUP_DISABLE;
    gpio_config(&io_conf);
    digital_pin[payload.gpio] = true;
    publish(message.request_id, ";", DIGITAL_SET_UP_ACK);
}

void digital_write_request(ParsedMessage message, DigitalWriteRequest payload)
{
    if (message.message_type != DIGITAL_WRITE_REQUEST)
    {
        return;
    }

    if (!digital_pin[payload.gpio])
    {
        ESP_LOGE(TAG, "Config digital for gpio %d is not setup", payload.gpio);
        return;
    }

    ESP_LOGI("DIGITAL", "Setup value: %d for pin: %d", payload.level == true ? 1 : 0, payload.gpio);
    gpio_set_level(payload.gpio, payload.level == true ? 1 : 0);
    publish(message.request_id, ";", DIGITAL_WRITE_RESPONSE);
}