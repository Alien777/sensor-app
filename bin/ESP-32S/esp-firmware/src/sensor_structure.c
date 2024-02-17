#include "sensor_structure.h"
#include "sensor_wifi.h"
#include "sensor_memory.h"
static char topic[50] = {0};
static void analog_read_json(cJSON *analog_reader_c, Message *msg);

#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "driver/ledc.h"
#include "esp_err.h"
#include "math.h"
#include "esp_log.h"

const char *message_type_convert_to_chars(message_type state)
{
    switch (state)
    {
    case DEVICE_CONNECTED:
        return "DEVICE_CONNECTED";
    case SINGLE_ADC_SIGNAL:
        return "SINGLE_ADC_SIGNAL";
    case CONFIG:
        return "CONFIG";
    default:
        return "UNKNOWN";
    }
}


message_type chars_convert_to_message_type(const char *state)
{
    if (strcmp(state, "DEVICE_CONNECTED") == 0)
    {
        return DEVICE_CONNECTED;
    }
    else if (strcmp(state, "SINGLE_ADC_SIGNAL") == 0)
    {
        return SINGLE_ADC_SIGNAL;
    }
    else if (strcmp(state, "CONFIG") == 0)
    {
        return CONFIG;
    }
    else
    {
        return UNKNOWN;
    }
}

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

esp_err_t json_to_message(const char *j, Message *msg)
{

    cJSON *json = cJSON_Parse(j);
    if (json == NULL)
    {
        const char *error_ptr = cJSON_GetErrorPtr();
        if (error_ptr != NULL)
        {
        }
        msg = NULL;
        return ESP_FAIL;
    }

    cJSON *member_key = cJSON_GetObjectItemCaseSensitive(json, MEMBER_KEY);
    if (member_key == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    cJSON *device_key = cJSON_GetObjectItemCaseSensitive(json, DEVICE_KEY);
    if (device_key == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    cJSON *msg_type = cJSON_GetObjectItemCaseSensitive(json, "message_type");
    if (msg_type == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    cJSON *config_id = cJSON_GetObjectItemCaseSensitive(json, "config_identifier");
    if (config_id == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    cJSON *version = cJSON_GetObjectItemCaseSensitive(json, "version_firmware");
    if (version == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    if (cJSON_IsNumber(config_id))
    {
        msg->config_id = config_id->valueint;
    }
    else
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    if (cJSON_IsString(version) && (version->valuestring != NULL))
    {
        strncpy(msg->version, version->valuestring, sizeof(msg->version) - 1);
    }
    else
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    if (cJSON_IsString(member_key) && (member_key->valuestring != NULL))
    {
        strncpy(msg->member_key, member_key->valuestring, sizeof(msg->member_key) - 1);
    }

    if (cJSON_IsString(device_key) && (device_key->valuestring != NULL))
    {
        strncpy(msg->device_key, device_key->valuestring, sizeof(msg->device_key) - 1);
    }
    if (cJSON_IsString(msg_type) && (msg_type->valuestring != NULL))
    {
        message_type type = chars_convert_to_message_type(msg_type->valuestring);
        if (type == UNKNOWN)
        {
            cJSON_Delete(json);
            return ESP_FAIL;
        }
        msg->message_type = type;
    }

    if (msg->message_type == CONFIG)
    {

        cJSON *payload_c = cJSON_GetObjectItem(json, "payload");
        if (payload_c == NULL)
        {
            cJSON_Delete(json);
            return ESP_FAIL;
        }
        cJSON *analog_reader_c = cJSON_GetObjectItemCaseSensitive(payload_c, "analog_reader");
        analog_read_json(analog_reader_c, msg);
    }

    cJSON_Delete(json);
    return ESP_OK;
}

void analog_read_json(cJSON *analog_reader_c, Message *msg)
{
    if (analog_reader_c != NULL && cJSON_IsArray(analog_reader_c))
    {
        int input_count = cJSON_GetArraySize(analog_reader_c);
        msg->analog_reader_size = MIN(input_count, MAX_S);
        for (int i = 0; i < msg->analog_reader_size; ++i)
        {
            cJSON *output_item = cJSON_GetArrayItem(analog_reader_c, i);
            if (output_item != NULL && cJSON_IsObject(output_item))
            {

              
                cJSON *pin_c = cJSON_GetObjectItemCaseSensitive(output_item, "pin");
                cJSON *width_c = cJSON_GetObjectItemCaseSensitive(output_item, "width");
                cJSON *atten_c = cJSON_GetObjectItemCaseSensitive(output_item, "atten");
                cJSON *sampling_c = cJSON_GetObjectItemCaseSensitive(output_item, "sampling");
                cJSON *min_adc_c = cJSON_GetObjectItemCaseSensitive(output_item, "min_adc");
                cJSON *max_adc_c = cJSON_GetObjectItemCaseSensitive(output_item, "max_adc");
        

                if (pin_c != NULL && cJSON_IsNumber(pin_c))
                {
                    ESP_LOGI("PIN READ ", "PIN READ %d", pin_c->valueint);
                    msg->analog_reader[i].pin = pin_c->valueint;
                }
                if (width_c != NULL && cJSON_IsNumber(width_c))
                {
                    msg->analog_reader[i].width = width_c->valuedouble;
                }
                if (atten_c != NULL && cJSON_IsNumber(atten_c))
                {
                    msg->analog_reader[i].atten = atten_c->valuedouble;
                }
                if (sampling_c != NULL && cJSON_IsNumber(sampling_c))
                {
                    msg->analog_reader[i].sampling = sampling_c->valueint;
                }
                if (min_adc_c != NULL && cJSON_IsNumber(min_adc_c))
                {
                    msg->analog_reader[i].min_adc = min_adc_c->valueint;
                }
                if (max_adc_c != NULL && cJSON_IsNumber(max_adc_c))
                {
                    msg->analog_reader[i].max_adc = max_adc_c->valueint;
                }
            }
        }
    }
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
    strncat(topic, config.member_key, sizeof(topic) - strlen(topic) - 1);
    strncat(topic, "/", sizeof(topic) - strlen(topic) - 1);
    strncat(topic, deviceKey, sizeof(topic) - strlen(topic) - 1);

    return topic;
}
