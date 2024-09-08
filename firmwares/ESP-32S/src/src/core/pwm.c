#include "core/pwm.h"

const static char *TAG = "PWM";

// Tablica do przypisywania kanałów do pinów GPIO
static int pwm_pin_to_channel[GPIO_NUM_MAX] = {[0 ... GPIO_NUM_MAX - 1] = -1};
TimerHandle_t pwm_timers[GPIO_NUM_MAX];

// Globalna tablica do zarządzania użyciem timerów
static int pwm_timer_usage[LEDC_TIMER_MAX] = {[0 ... LEDC_TIMER_MAX - 1] = 0}; 

void pwmStopCallback(TimerHandle_t xTimer);
static void pwm_reset_pin(int gpio);
static int get_free_pwm_channel();
int get_pwm_timer_num(int freq_hz);

void pwm_write_request(ParsedMessage message, PwmWriteRequest payload)
{
    if (message.message_type != PWM_WRITE_REQUEST)
    {
        return;
    }

    if (payload.gpio >= GPIO_NUM_MAX || payload.gpio < 0)
    {
        ESP_LOGE(TAG, "Invalid GPIO number pwm: %d", payload.gpio);
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

    esp_err_t err;
    err = ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, payload.duty);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to set duty cycle for channel: %d, error: %s", channel, esp_err_to_name(err));
        return;
    }

    err = ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to update duty cycle for channel: %d, error: %s", channel, esp_err_to_name(err));
        return;
    }
    publish(message.request_id, ";", PWM_WRITE_RESPONSE);
}

void pwm_write_set_up(ParsedMessage message, PwmWriteSetUp payload)
{
    if (message.message_type != PWM_WRITE_SET_UP)
    {
        return;
    }

    if (payload.gpio >= GPIO_NUM_MAX || payload.gpio < 0)
    {
        ESP_LOGE(TAG, "Invalid GPIO number: %d", payload.gpio);
        return;
    }

    pwm_reset_pin(payload.gpio);

    // Pobierz numer timera na podstawie częstotliwości
    int timer_num = get_pwm_timer_num(payload.frequency);

    if (timer_num == -1)
    {
        ESP_LOGE(TAG, "Failed to assign timer for GPIO %d", payload.gpio);
        return;
    }

    // Konfiguracja timera PWM
    ledc_timer_config_t ledc_timer = {
        .duty_resolution = payload.resolution,
        .freq_hz = payload.frequency,
        .speed_mode = LEDC_HIGH_SPEED_MODE,
        .timer_num = timer_num,
    };
    ledc_timer_config(&ledc_timer);

    // Konfiguracja kanału PWM
    int channel = get_free_pwm_channel();
    if (channel == -1)
    {
        ESP_LOGE(TAG, "No available PWM channels for GPIO %d", payload.gpio);
        return;
    }

    ledc_channel_config_t ledc_channel = {
        .channel = channel,
        .duty = payload.duty,
        .gpio_num = payload.gpio,
        .speed_mode = LEDC_HIGH_SPEED_MODE,
        .timer_sel = timer_num,
    };
    ledc_channel_config(&ledc_channel);

    pwm_pin_to_channel[payload.gpio] = channel;
    publish(message.request_id, ";", PWM_WRITE_SET_UP_ACK);
}

void pwm_reset_pin(int gpio)
{
    int channel = pwm_pin_to_channel[gpio];
    if (channel == -1)
    {
        ESP_LOGE(TAG, "PWM is not set up for GPIO %d", gpio);
        return;
    }

    ESP_LOGI(TAG, "Resetting PWM for GPIO %d (channel %d)", gpio, channel);

    esp_err_t err = ledc_set_duty(LEDC_HIGH_SPEED_MODE, channel, 0);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to set duty cycle to 0 for channel: %d, error: %s", channel, esp_err_to_name(err));
        return;
    }

    err = ledc_update_duty(LEDC_HIGH_SPEED_MODE, channel);
    if (err != ESP_OK)
    {
        ESP_LOGE(TAG, "Failed to update duty cycle for channel: %d, error: %s", channel, esp_err_to_name(err));
        return;
    }

    if (pwm_timers[channel] != NULL)
    {
        ESP_LOGI(TAG, "Stopping timer for channel: %d", channel);
        xTimerStop(pwm_timers[channel], 0);
        xTimerDelete(pwm_timers[channel], 0);
        pwm_timers[channel] = NULL;
    }

    pwm_pin_to_channel[gpio] = -1;

    ESP_LOGI(TAG, "PWM reset complete for GPIO %d", gpio);
}

int get_free_pwm_channel()
{
    // Przeszukaj wszystkie dostępne kanały i znajdź pierwszy wolny
    for (int channel = 0; channel < LEDC_CHANNEL_MAX; channel++)
    {
        bool is_channel_used = false;

        // Sprawdź czy któryś pin nie jest przypisany do tego kanału
        for (int gpio = 0; gpio < GPIO_NUM_MAX; gpio++)
        {
            if (pwm_pin_to_channel[gpio] == channel)
            {
                is_channel_used = true;
                break;
            }
        }

        // Jeśli kanał jest wolny, zwróć jego numer
        if (!is_channel_used)
        {
            return channel;
        }
    }

    // Jeśli nie ma wolnych kanałów, zwróć -1 jako błąd
    return -1;
}

int get_pwm_timer_num(int freq_hz)
{
    // Sprawdź, czy istnieje już timer z przypisaną częstotliwością
    for (int timer_num = 0; timer_num < LEDC_TIMER_MAX; timer_num++)
    {
        if (pwm_timer_usage[timer_num] == freq_hz)
        {
            ESP_LOGI(TAG, "Reusing existing timer %d for frequency %d Hz", timer_num, freq_hz);
            return timer_num;
        }
    }

    // Znajdź wolny timer, jeśli nie ma dopasowanego
    for (int timer_num = 0; timer_num < LEDC_TIMER_MAX; timer_num++)
    {
        if (pwm_timer_usage[timer_num] == 0) // Timer jest wolny
        {
            pwm_timer_usage[timer_num] = freq_hz; // Zajmij timer dla danej częstotliwości
            ESP_LOGI(TAG, "Assigning new timer %d for frequency %d Hz", timer_num, freq_hz);
            return timer_num;
        }
    }

    // Zwróć -1, jeśli nie ma dostępnych timerów
    ESP_LOGE(TAG, "No available timers for frequency %d Hz", freq_hz);
    return -1;
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
