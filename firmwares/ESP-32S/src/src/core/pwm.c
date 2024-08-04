#include "core/pwm.h"
#define MAX_PWM_PINS 100

const static char *TAG = "PWM";

static int pwm_pin_to_channel[MAX_PWM_PINS];

TimerHandle_t pwm_timers[MAX_PWM_PINS];
int pwm_channels[MAX_PWM_PINS];
bool is_time_extension[MAX_PWM_PINS];
int pwmTasks = 0;
void pwmStopCallback(TimerHandle_t xTimer);

void set_pwm(Message *message)
{
    if (message->message_type != PWM)
    {
        ESP_LOGI(TAG, "This not PWM message");
        return;
    }
    publish(message->config_id, message->request_id, ";", PWM_ACK);
    int duty = message->pwn_setup.duty;
    int duration = message->pwn_setup.duration;
    int pin = message->pwn_setup.pin;
    int channel = pwm_pin_to_channel[pin];

    ESP_LOGI(TAG, "Setup duty: %d duration: %d for pin: %d", duty, duration, pin);

    if (duration > 0)
    {
        if (pwm_timers[channel] != NULL)
        {
            ESP_LOGI(TAG, "Extending timer duration for channel: %d", channel);
            if (xTimerChangePeriod(pwm_timers[channel], pdMS_TO_TICKS(duration), 0) != pdPASS)
            {
                ESP_LOGE(TAG, "Failed to change timer period for channel: %d", channel);
            }
        }
        else
        {
            ESP_LOGI(TAG, "Creating new timer for channel: %d", channel);
            pwm_timers[channel] = xTimerCreate("pwm_timer", pdMS_TO_TICKS(duration), pdFALSE, (void *)channel, pwmStopCallback);
            if (pwm_timers[channel] != NULL)
            {
                if (xTimerStart(pwm_timers[channel], 0) != pdPASS)
                {
                    ESP_LOGE(TAG, "Failed to start timer for channel: %d", channel);
                }
            }
            else
            {
                ESP_LOGE(TAG, "Failed to create timer for channel: %d", channel);
            }
        }
    }
    ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, duty);
    ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);

}

void setup_pwm(Message *message)
{
    pwmTasks = message->pwm_configs_size;
    for (int i = 0; i < message->pwm_configs_size; i++)
    {

        ESP_LOGE(TAG, "Config pwm from pin %d %d", message->pwm_configs[i].pin, i);

        ledc_timer_config_t ledc_timer = {
            .duty_resolution = message->pwm_configs[i].resolution,
            .freq_hz = message->pwm_configs[i].freq,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_num = i};
        ledc_timer_config(&ledc_timer);

        ledc_channel_config_t ledc_channel = {
            .channel = i,
            .duty = 0,
            .gpio_num = message->pwm_configs[i].pin,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_sel = i};
        ledc_channel_config(&ledc_channel);

        pwm_pin_to_channel[message->pwm_configs[i].pin] = i;
        is_time_extension[message->pwm_configs[i].pin] = false;

        pwm_channels[i] = i;
    }
}
void reset_pwm_settings()
{

    for (int i = 0; i < 100; i++)
    {
        pwm_pin_to_channel[i] = -1;
        is_time_extension[i] = false;
    }

    for (int i = 0; i < pwmTasks; i++)
    {
        ledc_stop(LEDC_HIGH_SPEED_MODE, i, 0);

        ledc_timer_config_t ledc_timer_reset = {
            .duty_resolution = LEDC_TIMER_1_BIT,
            .freq_hz = 0,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .clk_cfg = LEDC_AUTO_CLK,
            .timer_num = i};
        ledc_timer_config(&ledc_timer_reset);

        ledc_channel_config_t ledc_channel_reset = {
            .channel = i,
            .duty = 0,
            .gpio_num = GPIO_NUM_NC,
            .speed_mode = LEDC_HIGH_SPEED_MODE,
            .timer_sel = i};
        ledc_channel_config(&ledc_channel_reset);

        if (pwm_timers[i] != NULL)
        {
            xTimerDelete(pwm_timers[i], 0);
            pwm_timers[i] = NULL;
        }
    }
    pwmTasks = 0;
}

void pwmStopCallback(TimerHandle_t xTimer)
{
    int channel = (int)pvTimerGetTimerID(xTimer);

    ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, 0);
    ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
    ESP_LOGI(TAG, "Duty cycle set to 0 for channel: %d", channel);

    pwm_timers[channel] = NULL; // Reset the timer in the array
}
