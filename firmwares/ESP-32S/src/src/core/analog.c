#include "core/analog.h"

const static char *TAG = "ANALOG";
static adc_oneshot_unit_handle_t adc_handle[GPIO_NUM_MAX] = {[0 ... GPIO_NUM_MAX - 1] = NULL};
static void analog_reset_pin(int gpio);
void analog_read_one_shot_request(ParsedMessage message, AnalogReadOneShotRequest paylaod)
{

    if (message.message_type != ANALOG_READ_ONE_SHOT_REQUEST)
    {
        return;
    }

    if (paylaod.gpio >= GPIO_NUM_MAX || paylaod.gpio < 0)
    {
        ESP_LOGE(TAG, "Invalid GPIO number analog: %d", paylaod.gpio);
        return;
    }

    int adc_raw = 0;

    adc_unit_t unit_id;
    adc_channel_t channel;
    esp_err_t ret;

    ret = adc_oneshot_io_to_channel(paylaod.gpio, &unit_id, &channel);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to map pin to channel: %s", esp_err_to_name(ret));
        return;
    }

    ESP_LOGI(TAG, "Mapped pin %d to unit %d, channel %d", paylaod.gpio, unit_id, channel);

    ret = adc_oneshot_read(adc_handle[paylaod.gpio], channel, &adc_raw);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "ADC read failed: %s", esp_err_to_name(ret));
        return;
    }
    char response[100];

    snprintf(response, sizeof(response), "%d;%d", adc_raw, paylaod.gpio);
    publish(message.request_id, response, ANALOG_READ_ONE_SHOT_RESPONSE);
}

void analog_read_set_up(ParsedMessage message, AnalogReadSetUp paylaod)
{

    if (message.message_type != ANALOG_READ_SET_UP)
    {
        return;
    }

    if (paylaod.gpio >= GPIO_NUM_MAX || paylaod.gpio < 0)
    {
        ESP_LOGE(TAG, "Invalid GPIO number analog: %d", paylaod.gpio);
        return;
    }

    analog_reset_pin(paylaod.gpio);

    ESP_LOGE(TAG, "Config analog from pin %d, resolution %d", paylaod.gpio, paylaod.resolution);

    adc_oneshot_unit_handle_t adc_handle_val = NULL;
    adc_unit_t unit_id;
    adc_channel_t channel;
    esp_err_t ret;

    ret = adc_oneshot_io_to_channel(paylaod.gpio, &unit_id, &channel);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to map pin to channel: %s", esp_err_to_name(ret));
        return;
    }

    adc_oneshot_unit_init_cfg_t init_config = {
        .unit_id = unit_id};
    ESP_ERROR_CHECK(adc_oneshot_new_unit(&init_config, &adc_handle_val));

    adc_oneshot_chan_cfg_t config = {
        .atten = ADC_ATTEN_DB_12,
        .bitwidth = paylaod.resolution,
    };
    ESP_ERROR_CHECK(adc_oneshot_config_channel(adc_handle_val, channel, &config));

    adc_handle[paylaod.gpio] = adc_handle_val;
    publish(message.request_id, "", ANALOG_READ_SET_UP_ACK);
}

void analog_reset_pin(int gpio)
{

    if (adc_handle[gpio] == NULL)
    {
        ESP_LOGE(TAG, "ADC handle is not set up for GPIO %d", gpio);
        return;
    }

    ESP_LOGI(TAG, "Resetting ADC for GPIO %d", gpio);

    esp_err_t ret = adc_oneshot_del_unit(adc_handle[gpio]);
    if (ret != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to delete ADC unit for GPIO %d, error: %s", gpio, esp_err_to_name(ret));
        return;
    }

    adc_handle[gpio] = NULL;

    ESP_LOGI(TAG, "ADC reset complete for GPIO %d", gpio);
}
