#include "core/pwm.h"

const static char *TAG = "PWM";

static int pwm_pin_to_channel[GPIO_NUM_MAX] = {[0 ... GPIO_NUM_MAX - 1] = -1};
TimerHandle_t pwm_timers[GPIO_NUM_MAX];

void pwmStopCallback(TimerHandle_t xTimer);

void pwm_write_request(ParsedMessage message, PwmWriteRequest payload)
{

    if (message.message_type != PWM_WRITE_REQUEST)
    {
        return;
    }

    ESP_LOGE(TAG, "Config PWM from pin %d", payload.gpio);

    if (pwm_pin_to_channel[payload.gpio] == -1)
    {
        ESP_LOGE(TAG, "Config PWM is not set up %d", payload.gpio);
        return;
    }

    ESP_LOGI(TAG, "Setup duty: %d duration: %d for pin: %d", payload.duty, payload.duration, payload.gpio);
    int channel = pwm_pin_to_channel[payload.gpio];
    if (payload.duration > 0)
    {
        if (pwm_timers[channel] != NULL)
        {
            ESP_LOGI(TAG, "Extending timer duration for channel: %d, %d", channel, payload.duration);
            if (xTimerChangePeriod(pwm_timers[channel], pdMS_TO_TICKS(payload.duration), 0) != pdPASS)
            {
                ESP_LOGE(TAG, "Failed to change timer period for channel: %d", channel);
            }
        }
        else
        {
            ESP_LOGI(TAG, "Creating new timer for channel: %d", channel);
            pwm_timers[channel] = xTimerCreate("pwm_timer", pdMS_TO_TICKS(payload.duration), pdFALSE, (void *)channel, pwmStopCallback);
            if (pwm_timers[channel] != NULL)
            {
                if (xTimerStart(pwm_timers[channel], 0) != pdPASS)
                {
                    ESP_LOGE(TAG, "Failed to start timer for channel: %d", channel);
                    xTimerDelete(pwm_timers[channel], 0);
                    pwm_timers[channel] = NULL;
                }
            }
            else
            {
                ESP_LOGE(TAG, "Failed to create timer for channel: %d", channel);
            }
        }
    }
    publish(message.request_id, ";", PWM_WRITE_RESPONSE);
    esp_err_t err;

    // Ustawienie wartości duty cycle
    err = ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, payload.duty);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to set duty cycle for channel: %d, error: %s", channel, esp_err_to_name(err));
        return; // Opcjonalnie, jeśli chcesz przerwać operację
    }

    // Aktualizacja wartości duty cycle
    err = ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to update duty cycle for channel: %d, error: %s", channel, esp_err_to_name(err));
        return; // Opcjonalnie, jeśli chcesz przerwać operację
    }
}

void pwm_write_set_up(ParsedMessage message, PwmWriteSetUp payload)
{

    if (message.message_type != PWM_WRITE_SET_UP)
    {
        return;
    }

    ESP_LOGE(TAG, "Config PWM from pin %d", payload.gpio);
    int channel_and_timmer = pwm_pin_to_channel[payload.gpio];
    if (channel_and_timmer == -1)
    {
        channel_and_timmer = 0;
        for (int i = 0; i < GPIO_NUM_MAX - 1; i++)
        {
            if (pwm_pin_to_channel[i] != -1)
            {
                channel_and_timmer++;
            }
        }
    }
    if (channel_and_timmer == 3)
    {
        ESP_LOGE(TAG, "Config PWM not available changel and timer %d", payload.gpio);
        return;
    }

    ledc_timer_config_t ledc_timer = {
        .duty_resolution = payload.resolution,
        .freq_hz = payload.frequency,
        .speed_mode = LEDC_HIGH_SPEED_MODE,
        .timer_num = channel_and_timmer};
    ledc_timer_config(&ledc_timer);

    ledc_channel_config_t ledc_channel = {
        .channel = channel_and_timmer,
        .duty = payload.duty,
        .gpio_num = payload.gpio,
        .speed_mode = LEDC_HIGH_SPEED_MODE,
        .timer_sel = channel_and_timmer};
    ledc_channel_config(&ledc_channel);

    pwm_pin_to_channel[payload.gpio] = channel_and_timmer;
    publish(message.request_id, ";", PWM_WRITE_SET_UP_ACK);
}

void pwmStopCallback(TimerHandle_t xTimer)
{
    int channel = (int)pvTimerGetTimerID(xTimer);

    ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, 0);
    ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
    ESP_LOGI(TAG, "Duty cycle set to 0 for channel: %d", channel);

    xTimerDelete(xTimer, 0);
    pwm_timers[channel] = NULL; // Reset the timer in the array
}
