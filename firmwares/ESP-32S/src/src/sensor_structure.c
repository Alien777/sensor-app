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
 
 

const char *convert_wifi_network_to_json(WifiNetwork *head)
{
    static char jsonBuffer[JSON_BUFFER_SIZE]; // Statyczny bufor zamiast malloc
    if (!head)
    {
        return NULL;
    }
    char *writePtr = jsonBuffer;
    *writePtr++ = '[';
    for (WifiNetwork *current = head; current != NULL; current = current->next)
    {
        if (current == NULL)
        {
            continue;
        }
        size_t remaining_space = JSON_BUFFER_SIZE - (writePtr - jsonBuffer);
        if (remaining_space < 100)
        {
            break;
        }
        int written = snprintf(writePtr, remaining_space,
                               "{\"name\":\"%s\",\"hasPassword\":%s,\"signalStrength\":%d},",
                               current->name, current->hasPassword ? "true" : "false", current->signalStrength);
        if (written < 0 || written >= remaining_space)
        {
            break;
        }
        writePtr += written;
    }
    if (writePtr != jsonBuffer && *(writePtr - 1) == ',')
    {
        writePtr--;
    }
    *writePtr++ = ']';
    *writePtr = '\0';
    return jsonBuffer;
}
 

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
