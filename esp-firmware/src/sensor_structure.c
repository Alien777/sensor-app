#include "sensor_structure.h"

const char *message_type_convert_to_chars(message_type state)
{
    switch (state)
    {
    case DEVICE_CONNECTED:
        return "DEVICE_CONNECTED";
    case SENSOR_VALUE:
        return "SENSOR_VALUE";
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
    else if (strcmp(state, "SENSOR_VALUE") == 0)
    {
        return SENSOR_VALUE;
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
        // Obliczanie pozostałej przestrzeni buforowej
        size_t remaining_space = JSON_BUFFER_SIZE - (writePtr - jsonBuffer);
        // Sprawdzenie, czy jest wystarczająco dużo miejsca na kolejny wpis
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
        writePtr--; // Usuwanie końcowego przecinka
    }
    *writePtr++ = ']';
    *writePtr = '\0';
    return jsonBuffer;
}

Message json_to_message(const char *j)
{
    Message msg = {0}; // Inicjalizacja struktury zerami
    cJSON *json = cJSON_Parse(j);
    if (json == NULL)
    {
        const char *error_ptr = cJSON_GetErrorPtr();
        if (error_ptr != NULL)
        {
            fprintf(stderr, "Błąd przed: %s\n", error_ptr);
        }
        return msg;
    }

    // Parsowanie i przypisywanie wartości do struktury
    cJSON *member_key = cJSON_GetObjectItemCaseSensitive(json, MEMBER_KEY);
    if (member_key == NULL)
    {
        cJSON_Delete(json);
        return msg;
    }
    cJSON *device_key = cJSON_GetObjectItemCaseSensitive(json, DEVICE_KEY);
    if (device_key == NULL)
    {
        cJSON_Delete(json);
        return msg;
    }
    cJSON *msg_type = cJSON_GetObjectItemCaseSensitive(json, "message_type");
    if (msg_type == NULL)
    {
        cJSON_Delete(json);
        return msg;
    }
    if (cJSON_IsString(member_key) && (member_key->valuestring != NULL))
    {
        strncpy(msg.member_key, member_key->valuestring, sizeof(msg.member_key) - 1);
    }

    if (cJSON_IsString(device_key) && (device_key->valuestring != NULL))
    {
        strncpy(msg.device_key, device_key->valuestring, sizeof(msg.device_key) - 1);
    }

    if (cJSON_IsString(msg_type) && (msg_type->valuestring != NULL))
    {
        message_type type = chars_convert_to_message_type(msg_type->valuestring);
        if (type == UNKNOWN)
        {
            cJSON_Delete(json);
            return msg;
        }
        msg.message_type = type;
    }

    cJSON_Delete(json);
    return msg;
}
