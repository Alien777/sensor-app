#include "sensor_structure.h"
#include "sensor_wifi.h"
#include "sensor_memory.h"
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/ledc.h"
#include "esp_err.h"
#include "math.h"
#include "esp_log.h"
 
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include "esp_system.h"
#include <unistd.h>
 
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


void generate_uuid(char *uuid_str) {
    // uint8_t uuid[16];

    // // Używamy esp_random() do generowania losowych bajtów, funkcja ta jest dostępna w ESP-IDF
    // for (int i = 0; i < 16; i++) {
    //     uuid[i] = (uint8_t)(esp_fill() & 0xFF);
    // }

    // // Formatujemy UUID zgodnie ze standardowym formatem UUID
    // sprintf(uuid_str, "%02x%02x%02x%02x-%02x%02x-%02x%02x-%02x%02x-%02x%02x%02x%02x%02x%02x",
    //         uuid[0], uuid[1], uuid[2], uuid[3],
    //         uuid[4], uuid[5],
    //         (uuid[6] & 0x0f) | 0x40, uuid[7],  // wersja 4
    //         (uuid[8] & 0x3f) | 0x80, uuid[9],  // wariant 1
    //         uuid[10], uuid[11], uuid[12], uuid[13], uuid[14], uuid[15]);
}
