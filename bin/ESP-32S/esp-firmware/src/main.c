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


    //  ledc_timer_config_t ledc_timer = {
    //         .duty_resolution = 12,
    //         .freq_hz = 16000,
    //         .speed_mode = LEDC_HIGH_SPEED_MODE,
    //         .timer_num = 1};
    //     ledc_timer_config(&ledc_timer);

    //     ledc_channel_config_t ledc_channel = {
    //         .channel = 1,
    //         .duty = 4000,        
    //         .gpio_num = 23,
    //         .speed_mode = LEDC_HIGH_SPEED_MODE,
    //         .timer_sel = 1};
    //     ledc_channel_config(&ledc_channel);

    //     // pwm_pin_to_channel[message->pwm_configs[i].pin] = i;

  start();
}
