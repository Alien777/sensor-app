#include "sensor_wifi.h"
#include "sensor_file_reader.h"
#include "sensor_memory.h"
#include "sensor_mqtt.h"
#include "sensor_server.h"
#include "driver/ledc.h"
#include "esp_err.h"
void start()
{
  file_system_initial();
  memory_initial();
  wifi_initial();
  connection_initial();
  server_initial();
  mqtt_initial();
}

void app_main()
{
  start();
}


// #include "freertos/FreeRTOS.h"
// #include "freertos/task.h"
// #include "driver/ledc.h"
// #include "driver/gpio.h"
// #include "esp_err.h"
// #include "esp_log.h"

// #define TAG "Motor"

// // Definicje pinów
// #define PWM_PIN 23
// #define EN_PIN 22

// void app_main(void)
// {
//     // Konfiguracja GPIO dla pinu ENABLE
//     gpio_config_t io_conf;
//     io_conf.intr_type = GPIO_INTR_DISABLE;
//     io_conf.mode = GPIO_MODE_OUTPUT;
//     io_conf.pin_bit_mask = (1ULL << EN_PIN);
//     io_conf.pull_down_en = GPIO_PULLDOWN_DISABLE;
//     io_conf.pull_up_en = GPIO_PULLUP_DISABLE;
//     gpio_config(&io_conf);

//     // Ustawienie pinu ENABLE na wysokim poziomie
//     gpio_set_level(EN_PIN, 1);

//     // Konfiguracja timerów dla PWM
//     ledc_timer_config_t pwm_timer = {
//         .speed_mode       = LEDC_HIGH_SPEED_MODE,
//         .timer_num        = LEDC_TIMER_0,
//         .duty_resolution  = LEDC_TIMER_10_BIT,
//         .freq_hz          = 1000,  // Częstotliwość PWM
//         .clk_cfg          = LEDC_AUTO_CLK
//     };

//     if (ledc_timer_config(&pwm_timer) != ESP_OK) {
//         ESP_LOGE(TAG, "PWM Timer configuration failed!");
//     } else {
//         ESP_LOGI(TAG, "PWM Timer configured successfully.");
//     }

//     // Konfiguracja kanału PWM
//     ledc_channel_config_t pwm_channel = {
//         .speed_mode     = LEDC_HIGH_SPEED_MODE,
//         .channel        = LEDC_CHANNEL_0,
//         .timer_sel      = LEDC_TIMER_0,
//         .gpio_num       = PWM_PIN,
//         .duty           = 128, // Ustawienie początkowego wypełnienia PWM (50%)
 
//     };

//     if (ledc_channel_config(&pwm_channel) != ESP_OK) {
//         ESP_LOGE(TAG, "PWM Channel configuration failed!");
//     } else {
//         ESP_LOGI(TAG, "PWM Channel configured successfully.");
//     }

//     // Inicjalizacja kanału PWM
//     if (ledc_fade_func_install(0) != ESP_OK) {
//         ESP_LOGE(TAG, "Failed to install fade function!");
//     } else {
//         ESP_LOGI(TAG, "Fade function installed successfully.");
//     }

//     // Ustawienie początkowego wypełnienia na 50%
//     ledc_set_duty(LEDC_HIGH_SPEED_MODE, LEDC_CHANNEL_0, 128);
//     ledc_update_duty(LEDC_HIGH_SPEED_MODE, LEDC_CHANNEL_0);
  
// }
