#include "core/analog.h"

#define MAX_TASKS 5

const static char *TAG = "ANALOG";

int taskCount = 0;

static adc_oneshot_unit_handle_t adc_handle[40] = {NULL};
static int channel[40] = {-1};

void request_for_analog_value(Message *message)
{
    if (message->message_type != ANALOG)
    {
        ESP_LOGI(TAG, "This is not an ANALOG message");
        return;
    }
    if (adc_handle[message->analog_read_data.pin] == NULL || channel[message->analog_read_data.pin] == -1)
    {
        ESP_LOGE(TAG, "ADC handle or channel not configured for pin: %d", message->analog_read_data.pin);
        return;
    }

    ESP_LOGI(TAG, "For pin: %d", message->analog_read_data.pin);
    int adc_raw = 0;

    ESP_ERROR_CHECK(adc_oneshot_read(adc_handle[message->analog_read_data.pin], channel[message->analog_read_data.pin], &adc_raw));
    char response[100];
    snprintf(response, sizeof(response), "%d;%d", adc_raw, message->analog_read_data.pin);
    publish(message->config_id, message->request_id, response, ANALOG_ACK);
}

void setup_analog(Message *message)
{
    for (int i = 0; i < message->analog_configs_size; i++)
    {
        int pin = message->analog_configs[i].pin;
        ESP_LOGE(TAG, "Config analog from pin %d", pin);

        adc_oneshot_unit_handle_t temp_adc_handle;
        adc_oneshot_unit_init_cfg_t init_cfg = {
            .unit_id = ADC_UNIT_1,
            .clk_src = ADC_DIGI_CLK_SRC_DEFAULT,
        };
        ESP_ERROR_CHECK(adc_oneshot_new_unit(&init_cfg, &temp_adc_handle));


        adc_oneshot_chan_cfg_t chan_cfg = {
            .bitwidth = message->analog_configs[i].width,
            .atten = message->analog_configs[i].atten,
        };
        ESP_ERROR_CHECK(adc_oneshot_config_channel(temp_adc_handle, i, &chan_cfg));
        adc_handle[pin] = temp_adc_handle;
        channel[pin] = i;
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
            channel[i] = -1;
        }
    }
}
