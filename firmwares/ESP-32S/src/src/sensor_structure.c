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
static void analog_read_json(cJSON *analog_reader_c, Message *msg);
static void pwm_read_json(cJSON *pwm_configs_c, Message *msg);
static void digital_read_json(cJSON *digital_configs_c, Message *msg);
const char *message_type_convert_to_chars(message_type state)
{
    switch (state)
    {
    case DEVICE_CONNECTED:
        return "DEVICE_CONNECTED";
    case ANALOG:
        return "ANALOG";
    case CONFIG:
        return "CONFIG";
    case ANALOG_EXTORT:
        return "ANALOG_EXTORT";
    case PWM:
        return "PWM";
    case DIGITAL_WRITE:
        return "DIGITAL_WRITE";
    case PING:
        return "PING";
    case PING_ACK:
        return "PING_ACK";
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
    else if (strcmp(state, "ANALOG") == 0)
    {
        return ANALOG;
    }
    else if (strcmp(state, "CONFIG") == 0)
    {
        return CONFIG;
    }
    else if (strcmp(state, "ANALOG_EXTORT") == 0)
    {
        return ANALOG_EXTORT;
    }
    else if (strcmp(state, "PWM") == 0)
    {
        return PWM;
    }
    else if (strcmp(state, "DIGITAL_WRITE") == 0)
    {
        return DIGITAL_WRITE;
    }
    else if (strcmp(state, "PING") == 0)
    {
        return PING;
    }
    else if (strcmp(state, "PING_ACK") == 0)
    {
        return PING_ACK;
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
    if (member_key == NULL || !cJSON_IsString(member_key) || (member_key->valuestring == NULL))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        strncpy(msg->member_key, member_key->valuestring, sizeof(msg->member_key) - 1);
    }

    cJSON *device_key = cJSON_GetObjectItemCaseSensitive(json, DEVICE_KEY);
    if (device_key == NULL || !cJSON_IsString(device_key) || (device_key->valuestring == NULL))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        strncpy(msg->device_key, device_key->valuestring, sizeof(msg->device_key) - 1);
    }

    cJSON *token = cJSON_GetObjectItemCaseSensitive(json, "token");
    if (token == NULL || !cJSON_IsString(token) || (token->valuestring == NULL))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        strncpy(msg->token, token->valuestring, sizeof(msg->token) - 1);
    }

    cJSON *msg_type = cJSON_GetObjectItemCaseSensitive(json, "message_type");
    if (msg_type == NULL || !cJSON_IsString(msg_type) || (msg_type->valuestring == NULL))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        message_type type = chars_convert_to_message_type(msg_type->valuestring);
        if (type == UNKNOWN)
        {
            cJSON_Delete(json);
            return ESP_FAIL;
        }
        msg->message_type = type;
    }

    cJSON *config_id = cJSON_GetObjectItemCaseSensitive(json, "config_identifier");
    if (config_id == NULL || !cJSON_IsNumber(config_id))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        msg->config_id = config_id->valueint;
    }

    cJSON *version = cJSON_GetObjectItemCaseSensitive(json, "version_firmware");
    if (version == NULL || !cJSON_IsString(version) || (version->valuestring == NULL))
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }
    else
    {
        strncpy(msg->version, version->valuestring, sizeof(msg->version) - 1);
    }

    ESP_LOGI("RECIVED", "Type message of received %d", msg->message_type);
    cJSON *payload_c = cJSON_GetObjectItem(json, "payload");
    if (payload_c == NULL)
    {
        cJSON_Delete(json);
        return ESP_FAIL;
    }

    if (msg->message_type == CONFIG)
    {

        cJSON *analog_configs_c = cJSON_GetObjectItemCaseSensitive(payload_c, "analog_configs");
        analog_read_json(analog_configs_c, msg);

        cJSON *pwm_reader_c = cJSON_GetObjectItemCaseSensitive(payload_c, "pwm_configs");
        pwm_read_json(pwm_reader_c, msg);

        cJSON *digital_reader_c = cJSON_GetObjectItemCaseSensitive(payload_c, "digital_configs");
        digital_read_json(digital_reader_c, msg);
    }
    else if (msg->message_type == PWM)
    {
        cJSON *pin_pwm_c = cJSON_GetObjectItemCaseSensitive(payload_c, "pin");
        cJSON *duty_pwm_c = cJSON_GetObjectItemCaseSensitive(payload_c, "duty");
        cJSON *duration_pwm_c = cJSON_GetObjectItemCaseSensitive(payload_c, "duration");
        if (pin_pwm_c != NULL && cJSON_IsNumber(pin_pwm_c))
        {
            msg->pwn_setup.pin = pin_pwm_c->valueint;
        }
        if (duty_pwm_c != NULL && cJSON_IsNumber(duty_pwm_c))
        {
            msg->pwn_setup.duty = duty_pwm_c->valueint;
        }
        if (duration_pwm_c != NULL && cJSON_IsNumber(duration_pwm_c))
        {
            msg->pwn_setup.duration = duration_pwm_c->valueint;
        }
    }
    else if (msg->message_type == ANALOG_EXTORT)
    {
        cJSON *pin_pwm_c = cJSON_GetObjectItemCaseSensitive(payload_c, "pin");

        if (pin_pwm_c != NULL && cJSON_IsNumber(pin_pwm_c))
        {
            msg->analog_read_data.pin = pin_pwm_c->valueint;
        }
    }
    else if (msg->message_type == DIGITAL_WRITE)
    {
        cJSON *pin_digital_c = cJSON_GetObjectItemCaseSensitive(payload_c, "pin");
        cJSON *value_digital_c = cJSON_GetObjectItemCaseSensitive(payload_c, "value");

        if (pin_digital_c != NULL && cJSON_IsNumber(pin_digital_c))
        {
            msg->digital_setup.pin = pin_digital_c->valueint;
        }
        if (value_digital_c != NULL && cJSON_IsNumber(value_digital_c))
        {
            msg->digital_setup.value = value_digital_c->valueint;
        }
    }

    cJSON_Delete(json);
    return ESP_OK;
}

void pwm_read_json(cJSON *pwm_configs_c, Message *msg)
{
    if (pwm_configs_c != NULL && cJSON_IsArray(pwm_configs_c))
    {
        int input_count = cJSON_GetArraySize(pwm_configs_c);
        msg->pwm_configs_size = MIN(input_count, MAX_S);
        for (int i = 0; i < msg->pwm_configs_size; ++i)
        {
            cJSON *output_item = cJSON_GetArrayItem(pwm_configs_c, i);
            if (output_item != NULL && cJSON_IsObject(output_item))
            {

                cJSON *pin_c = cJSON_GetObjectItemCaseSensitive(output_item, "pin");
                cJSON *wfreq_c = cJSON_GetObjectItemCaseSensitive(output_item, "freq");
                cJSON *resolution_c = cJSON_GetObjectItemCaseSensitive(output_item, "resolution");

                if (pin_c != NULL && cJSON_IsNumber(pin_c))
                {
                    msg->pwm_configs[i].pin = pin_c->valueint;
                }
                if (wfreq_c != NULL && cJSON_IsNumber(wfreq_c))
                {
                    msg->pwm_configs[i].freq = wfreq_c->valueint;
                }
                if (resolution_c != NULL && cJSON_IsNumber(resolution_c))
                {
                    msg->pwm_configs[i].resolution = resolution_c->valueint;
                }
            }
        }
    }
}

void digital_read_json(cJSON *digital_configs_c, Message *msg)
{
    if (digital_configs_c != NULL && cJSON_IsArray(digital_configs_c))
    {
        int input_count = cJSON_GetArraySize(digital_configs_c);
        msg->digital_configs_size = MIN(input_count, MAX_S);
        for (int i = 0; i < msg->digital_configs_size; ++i)
        {
            cJSON *output_item = cJSON_GetArrayItem(digital_configs_c, i);
            if (output_item != NULL && cJSON_IsObject(output_item))
            {

                cJSON *pin_c = cJSON_GetObjectItemCaseSensitive(output_item, "pin");

                if (pin_c != NULL && cJSON_IsNumber(pin_c))
                {
                    msg->digital_configs[i].pin = pin_c->valueint;
                }
            }
        }
    }
}

void analog_read_json(cJSON *analog_configs_c, Message *msg)
{
    if (analog_configs_c != NULL && cJSON_IsArray(analog_configs_c))
    {
        int input_count = cJSON_GetArraySize(analog_configs_c);
        msg->analog_configs_size = MIN(input_count, MAX_S);
        for (int i = 0; i < msg->analog_configs_size; ++i)
        {
            cJSON *output_item = cJSON_GetArrayItem(analog_configs_c, i);
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
                    msg->analog_configs[i].pin = pin_c->valueint;
                }
                if (width_c != NULL && cJSON_IsNumber(width_c))
                {
                    msg->analog_configs[i].width = width_c->valueint;
                }
                if (atten_c != NULL && cJSON_IsNumber(atten_c))
                {
                    msg->analog_configs[i].atten = atten_c->valueint;
                }
                else
                {
                    msg->analog_configs[i].atten = 3;
                }
                if (sampling_c != NULL && cJSON_IsNumber(sampling_c))
                {
                    msg->analog_configs[i].sampling = sampling_c->valueint;
                }
                else
                {
                    msg->analog_configs[i].sampling = 0;
                }
                if (min_adc_c != NULL && cJSON_IsNumber(min_adc_c))
                {
                    msg->analog_configs[i].min_adc = min_adc_c->valueint;
                }
                else
                {
                    msg->analog_configs[i].min_adc = 0;
                }
                if (max_adc_c != NULL && cJSON_IsNumber(max_adc_c))
                {
                    msg->analog_configs[i].max_adc = max_adc_c->valueint;
                }
                else
                {
                    msg->analog_configs[i].max_adc = 0;
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
    strncat(topic, config.member_id, sizeof(topic) - strlen(topic) - 1);
    strncat(topic, "/", sizeof(topic) - strlen(topic) - 1);
    strncat(topic, deviceKey, sizeof(topic) - strlen(topic) - 1);

    return topic;
}
