#include "sensor_structure.h"
#include "sensor_wifi.h"
#include "sensor_memory.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/ledc.h"
#include "esp_err.h"
#include "math.h"
#include "esp_log.h"

static char topic[50] = {0};
 
const char *topicSubscribe()
{
    ConfigEps config;
    if (load_config(&config) == ESP_FAIL)
    {
        return NULL;
    }
    const char *deviceKey = get_mac_address();
    memset(topic, 0, sizeof(topic));
    strncat(topic, "/", sizeof(topic) - 1);
    strncat(topic, config.member_id, sizeof(topic) - strlen(topic) - 1);
    strncat(topic, "/", sizeof(topic) - strlen(topic) - 1);
    strncat(topic, deviceKey, sizeof(topic) - strlen(topic) - 1);

    return topic;
}
