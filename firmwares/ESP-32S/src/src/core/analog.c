#include "core/analog.h"

const static char *TAG = "ANALOG";
static adc_oneshot_unit_handle_t adc_handle[40] = {NULL};

void request_for_analog_value(Message *message)
{

    if (message->message_type != ANALOG)
    {
        ESP_LOGI(TAG, "This is not an ANALOG message");
        return;
    }

    int pin = message->analog_read_data.pin;

    if (adc_handle[pin] == NULL)
    {
        ESP_LOGE(TAG, "ADC handle or channel not configured for pin: %d", pin);
        return;
    }

    ESP_LOGI(TAG, "For pin: %d", pin);
    int adc_raw = 0;

    adc_unit_t unit_id;
    adc_channel_t channel;
    esp_err_t ret;

    ret = adc_oneshot_io_to_channel(pin, &unit_id, &channel);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to map pin to channel: %s", esp_err_to_name(ret));
        return;
    }

    ESP_LOGI(TAG, "Mapped pin %d to unit %d, channel %d", pin, unit_id, channel);

    ret = adc_oneshot_read(adc_handle[pin], channel, &adc_raw);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "ADC read failed: %s", esp_err_to_name(ret));
        return;
    }
    char response[100];

    snprintf(response, sizeof(response), "%d;%d", adc_raw, pin);
    publish(message->config_id, message->request_id, response, ANALOG_ACK);
}

void setup_analog(Message *message)
{
    for (int i = 0; i < message->analog_configs_size; i++)
    {

        int pin = message->analog_configs[i].pin;
        int atten = message->analog_configs[i].atten;
        int width = message->analog_configs[i].width;
        ESP_LOGE(TAG, "Config analog from pin %d, width %d, atten %d", pin, width, atten);

        adc_oneshot_unit_handle_t adc_handle_val = NULL;
        adc_unit_t unit_id;
        adc_channel_t channel;
        esp_err_t ret;

        ret = adc_oneshot_io_to_channel(pin, &unit_id, &channel);
        if (ret != ESP_OK)
        {
            ESP_LOGE(TAG, "Failed to map pin to channel: %s", esp_err_to_name(ret));
            continue;
        }

        adc_oneshot_unit_init_cfg_t init_config = {
            .unit_id = unit_id};
        ESP_ERROR_CHECK(adc_oneshot_new_unit(&init_config, &adc_handle_val));

        adc_oneshot_chan_cfg_t config = {
            .atten = atten,
            .bitwidth = width,
        };
        ESP_ERROR_CHECK(adc_oneshot_config_channel(adc_handle_val, channel, &config));

        adc_handle[pin] = adc_handle_val;
    }
}

void reset_analog_settings()
{
    for (int i = 0; i < 40; i++)
    {
        if (adc_handle[i] != NULL)
        {
            ESP_ERROR_CHECK(adc_oneshot_del_unit(adc_handle[i]));
            adc_handle[i] = NULL;
        }
    }
}
